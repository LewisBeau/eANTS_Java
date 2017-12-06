package com.eants.communication;

/**
 * Created by lewis on 02/09/2017.
 * <p>
 * This class represents the Arduino connected via USB.
 */

import com.eants.gui.UpdateListener;
import com.eants.robots.RobotUpdate;
import com.eants.robots.RobotUpdater;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;


public class Arduino implements SerialPortEventListener {

    //the serial connection
    SerialPort serialPort;

    //Stuff for reading input
    private DataInputStream input;
    private InputStream inputStream;
    private byte[] readBuffer = new byte[400];

    //The output stream to the port
    private OutputStream output;
    private BufferedOutputStream bufferedOutput;

    //Milliseconds to block while waiting for port open
    private static final int TIME_OUT = 2000;
    //The time at which the port was opened
    private static long openTime = 0;
    //Default bits per second for COM port.
    private static final int DATA_RATE = 9600;

    //Array list containing all the ports which have been enumerated
    private ArrayList<CommPortIdentifier> comPorts = new ArrayList<>();

    //The com port used in communication
    private CommPortIdentifier portId;

    //Object which listens for updates
    private UpdateListener updateListener;

    //Object to which to send updated robot information
    private volatile RobotUpdater robotUpdater;

    //Contains all the pending requests
    private volatile ArrayList<ComRequest> requests = new ArrayList<>();

    private volatile ArrayList<Integer> incomingBytes = new ArrayList<>();

    //If the device is currently being programmed
    private volatile boolean programming = false;



    /**
     * responsible for sending pending commands to the arduino
     * also responsible for reading incoming bytes and decoding the info
     * and updating the robots through the robotUpdater object
     * @param robotUpdater
     */
    public Arduino (RobotUpdater robotUpdater){
        this.robotUpdater = robotUpdater;

        /*
        The following code is responsible for sending pending communications
        requests to the arduino for transmitting
         */

        /**
         * This thread takes the new command from the requests byte list and
         * then sends them to the arduino over the serial connection.
         * It runs in a new thread to make it independent of the thread making
         * the communications request
         */
        Thread writerThread = new Thread(() -> {
            //Run indefinitely
            while (true){
                if (serialPort!=null){
                    //New requests available
                    //if (serialPort.isCTS()) System.out.println("clear");
                    if (requests.size()>0 && !programming){
                        byte[] bytes = requests.get(0).getBytes();
                        for (byte message:bytes){
                            writeByte(message);
                            //Add delay after sending, if the request has a response
                            //This is not really necessary, as the arduino handles half duplex
                            //communication, but it also acts as a kind of buffer
                            if (message == CommunicationsController.REQUEST_DATA){
                                try{
                                    Thread.sleep(1); //1ms is plenty of time
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        //Remove sent bytes
                        requests.remove(0);
                    }else {
                        try{
                            Thread.sleep(10);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        writerThread.start();

        /**
         * this thread is responsible for taking incoming bytes from the serial connection
         * and then decoding them into robot status updates. The incoming bytes are added to the incomingBytes list
         * directly from the arduino. Therefore an entire message may not appear at once.
         * The messages come in the following format:
         * 1. robot address (1-200)
         * 2. Begin byte
         * 3. byte address (status, speed, etc.)
         * 4. byte value
         * 5. end byte
         *
         * Steps 4 and 5 may be repeated up to six times.
         * The data is then parsed from the input and sent to the RobotUpdater interface for adding to the window
         * and/or logging.
         */
        Thread readerThread = new Thread(() -> {
            while (true){
                //Check if the list contains start and end bytes and is long enough
                if (    incomingBytes.contains(byteToInt(CommunicationsController.BEGIN_BYTE)) &&
                        incomingBytes.contains(byteToInt(CommunicationsController.END_BYTE))){


                    //Convert to array
                    int[] incoming = new int[incomingBytes.size()];
                    for (int i = 0;i < incoming.length;i++){
                        incoming[i] = incomingBytes.get(i);
                    }
                    //tell whether an information precursor has been sent
                    boolean request1Set = false;
                    boolean request2Set = false;
                    boolean request3Set = false;
                    boolean request4Set = false;
                    boolean request5Set = false;
                    boolean request6Set = false;
                    boolean addressSet = false;
                    boolean beginSet = false;

                    //The robot id
                    int address = -1;

                    //The values storing the new information
                    int value1 = -1;
                    int value2 = -1;
                    int value3 = -1;
                    int value4 = -1;
                    int value5 = -1;
                    int value6 = -1;

                    for(int i = 0;i < incoming.length;i++){
                        int message = incoming[i];
                        //Check for valid address
                        if (!addressSet){
                            if (message>=0 && message<=200){
                                addressSet = true;
                                address = message;
                            }
                            //Check if begin bit follows
                        }else if (!beginSet){
                            if (message == byteToInt(CommunicationsController.BEGIN_BYTE)){
                                beginSet = true;
                            }else {
                                address = -1;
                                addressSet = false;
                            }
                            //Check for new data incoming
                        }else if(beginSet){
                            //Handle byte precursor for new information
                            if (!request1Set && !request2Set && !request3Set && !request4Set &&
                                    !request5Set && !request6Set){
                                if (message == byteToInt(CommunicationsController.STATUS1_BYTE)){
                                    request1Set = true;
                                } else if (message == byteToInt(CommunicationsController.STATUS2_BYTE)){
                                    request2Set = true;
                                } else if (message == byteToInt(CommunicationsController.STATUS3_BYTE)){
                                    request3Set = true;
                                } else if (message == byteToInt(CommunicationsController.CHARGE_BYTE)){
                                    request4Set = true;
                                } else if (message == byteToInt(CommunicationsController.SPEED_BYTE)){
                                    request5Set = true;
                                }else if (message == byteToInt(CommunicationsController.ORIENTATION_BYTE)){
                                    request6Set = true;
                                }else if(message != byteToInt(CommunicationsController.END_BYTE)){
                                    addressSet = false;
                                    beginSet = false;
                                    //Remove used bytes
                                }
                                //If one of the precursor values has been set, receive the data and
                                //store it temporarily
                            }else if (request1Set){
                                value1 = message;
                                request1Set = false;
                            }else if (request2Set){
                                value2 = message;
                                request2Set = false;
                            }else if (request3Set){
                                value3 = message;
                                request3Set = false;
                            }else if (request4Set){
                                value4 = message;
                                request4Set = false;
                            }else if (request5Set){
                                value5 = message;
                                request5Set = false;
                            }else if (request6Set){
                                value6 = message;
                                request6Set = false;
                            }
                        }
                        //Write received data to robot updater
                        if (message == byteToInt(CommunicationsController.END_BYTE) &&
                            beginSet){
                            RobotUpdate robotUpdate = new RobotUpdate(address);
                            if (value1 != -1)robotUpdate.setStatus1(value1);
                            if (value2 != -1)robotUpdate.setStatus2(value2);
                            if (value3 != -1)robotUpdate.setStatus3(value3);
                            if (value4 != -1)robotUpdate.setCharge(value4);
                            if (value5 != -1)robotUpdate.setSpeed(value5);
                            if (value6 != -1)robotUpdate.setOrientation(value6);
                            robotUpdater.updateState(robotUpdate);
                        }
                        //Remove all bytes before end byte
                        if (message == byteToInt(CommunicationsController.END_BYTE)){
                            for (int e = 0;e < i;e++){
                                incomingBytes.remove(0);
                            }
                        }
                    }

                }

            }
        });
        readerThread.start();
    }


    /**
     * Converts byte to unsigned char value (positive int)
     * @param message the byte
     * @return the positive unsigned char value
     */
    private synchronized int byteToInt(byte message){
        return Arduino.getUnsignedByte(message);
    }


    /**
     * Enumerate all the ports and then return a string array with the values
     * @return
     */
    public String[] enumeratePorts() {

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        comPorts.clear();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            comPorts.add(currPortId);
        }

        String[] portNames = new String[comPorts.size()];
        for (int i = 0;i < portNames.length;i++){
            portNames[i] = comPorts.get(i).getName();
        }
        return portNames;

    }

    /**
     * Open the serial port at the given COM port
     * @param index
     */
    public void open(int index) {

        try {
            enumeratePorts();
            portId = comPorts.get(index);
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);


            // open the streams
            //input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            input = new DataInputStream(new BufferedInputStream(serialPort.getInputStream()));
            inputStream = serialPort.getInputStream();
            //inputStream = new InputStreamReader(serialPort.getInputStream());
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            serialPort.notifyOnOutputEmpty(true);
            serialPort.notifyOnCTS(true);
            serialPort.notifyOnDSR(true);

            updateListener.connectedStateChange(1);

            openTime = System.currentTimeMillis();

        } catch (Exception e) {
            System.err.println(e.toString());
            updateListener.connectedStateChange(0);

        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
            updateListener.connectedStateChange(0);
        }
    }

    /**
     * Tells if the connection is open
     * @return boolean saying if true
     */
    public boolean isOpen(){
        if (serialPort != null){
            updateListener.connectedStateChange(1);
            return true;
        }else {
            updateListener.connectedStateChange(0);
            return false;
        }
    }

    /**
     * Tells if the arduino is currently uploading code
     * @return true for programming, false for idle
     */
    public boolean isProgramming(){
        return programming;
    }

    /**
     * Used for adding a new request to the communication requests
     * @param comRequest the desired communication
     */
    public synchronized void addRequest(ComRequest comRequest){
        if (isOpen()){
            if (comRequest.isProgramRequest()){
                //Program request have utmost priority, all other requests are cleared temporarily
                requests.clear();
                program(comRequest);
            }else {
                if (!programming){
                    //Add request at top of list after other import requests, if it is important
                    if (comRequest.isImportant()){
                        if (requests.size()>0){
                            int index = -1;
                            do{
                                index++;
                            }while (requests.get(index).isImportant() && requests.size()>index);
                            requests.add(index, comRequest);
                        }else {
                            requests.add(comRequest);
                        }
                    }else { //Otherwise add at end of list
                        requests.add(comRequest);
                    }
                    updateListener.queueLength(requests.size());
                }
            }
        }
    }

    /**
     * Uploads new code
     * @param comRequest The code to be uploaded
     */
    private void program(ComRequest comRequest){
        programming = true;
        updateListener.isProgramming(true);

        byte[] bytes = comRequest.getBytes();
        for (byte b:bytes){
            writeByte(b);
        }

        //serialPort.

        updateListener.isProgramming(false);

    }

    /**
     * This code writes a byte to the arduino
     * @param message the byte to be sent
     */
    private synchronized void writeByte(byte message){
        try {
            if ((System.currentTimeMillis()-openTime)<TIME_OUT){
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if (message >= -128 && message <= 128){
                output.write(message);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateListener.connectedStateChange(0);

        }
    }

    public static int getUnsignedByte(byte message){
        return message>=0 ? message : 256+message;
    }

    /**
     *This code writes bytes to the arduino
     * @param messages the byte array to be sent
     */
    private synchronized void writeBytes(byte[] messages){
        for (byte message : messages){
            try {
                if (message >= -128 && message <= 128){
                    int messageInt = getUnsignedByte(message);  //Convert to unsigned value
                    output.write(messageInt);
                    output.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                updateListener.connectedStateChange(0);

            }
        }
    }


    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {

        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                int availableBytes = inputStream.available();
                if (availableBytes > 0) {
                    // Read the serial port
                    inputStream.read(readBuffer, 0, availableBytes);

                    for (int i = 0; i < availableBytes; i++) {
                        int value = readBuffer[i];
                        value = value >= 0 ? value : 256 + value;
                        incomingBytes.add(value);
                        //System.out.println(value);
                    }
                }


            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }if (oEvent.getEventType() == SerialPortEvent.DSR){
            System.out.println("Buffer empty");
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }


    /**
     * Sets the updatelistener, onto which updates to the connection are registered
     * @param updateListener
     */
    public void setUpdateListener(UpdateListener updateListener){
        this.updateListener = updateListener;
    }




}