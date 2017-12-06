package com.eants.communication;

import com.eants.robots.Robot;
import com.eants.robots.RobotUpdater;

import javax.print.DocFlavor;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by lewis on 03/09/2017.
 *
 * This class communicated with the arduino
 */
public class CommunicationsController {

    /**
     * All of the bytes for performing various tasks
     */
    //Communication
    final public static byte BEGIN_BYTE = (byte) 0b11001001;  //201
    final public static byte END_BYTE = (byte) 0b11001010;
    //Addresses
    final public static byte ALL_ROBOTS_BYTE = (byte) 0b11001011; //203
    final public static byte ADDRESS_GROUP1_BYTE = (byte) 0b11001100;
    final public static byte ADDRESS_GROUP2_BYTE = (byte) 0b11001101;
    final public static byte ADDRESS_GROUP3_BYTE = (byte) 0b11001110;
    //Programming
    final public static byte BOOTLOADER_BYTE = (byte) 0b11001111; //207
    //Control
    final public static byte HIBERNATE_BYTE = (byte) 0b11010000;
    final public static byte CHANGE_ID_BYTE = (byte) 0b11010001;

    final public static byte ENABLE_MANUAL_BYTE = (byte) 0b11010010;  //210
    final public static byte DISABLE_MANUAL_BYTE = (byte) 0b11010011;
    final public static byte MOVE_FORWARDS_BYTE = (byte) 0b11010100;
    final public static byte MOVE_BACKWARDS_BYTE = (byte) 0b11010101;
    final public static byte STOP_BYTE = (byte) 0b11010110;
    final public static byte ROTATE_RIGHT_BYTE = (byte) 0b11010111;
    final public static byte ROTATE_LEFT_BYTE = (byte) 0b11011000;

    final public static byte COMMAND1_BYTE = (byte) 0b11011001;  //217
    final public static byte COMMAND2_BYTE = (byte) 0b11011010;
    final public static byte COMMAND3_BYTE = (byte) 0b11011011;

    final public static byte SENSE1_BYTE = (byte) 0b11011100;  //220
    final public static byte SENSE2_BYTE = (byte) 0b11011101;
    final public static byte SENSE3_BYTE = (byte) 0b11011110;
    //Requests
    final public static byte STATUS1_BYTE = (byte) 0b11011111;  //223
    final public static byte STATUS2_BYTE = (byte) 0b11100000;
    final public static byte STATUS3_BYTE = (byte) 0b11100001;
    final public static byte CHARGE_BYTE = (byte) 0b11100010;
    final public static byte SPEED_BYTE = (byte) 0b11100011;
    final public static byte ORIENTATION_BYTE = (byte) 0b11100100;
    final public static byte REQUEST_DATA = (byte) 0b11100101;
    //Error
    final public static byte MESSAGE_ERROR_BYTE = (byte) 0b11111111;



    private Arduino arduino;

    /**
     * This implemented listener send requests for information
     */
    private CommunicationsRequester communicationsRequester;

    /**
     * This is the object to which updates should be sent
     */
    private RobotUpdater robotUpdater;

    /**
     * Currently programming or not
     */
    private boolean isProgramming = false;


    /**
     * This class handles requests to send and retrieve things from the robots
     * @param arduino the arduino used as an intermediary communicator
     * @param robotUpdater the listener which allows the user/GUI to request info/programming
     */
    public CommunicationsController(Arduino arduino, RobotUpdater robotUpdater){

        this.arduino = arduino;

        communicationsRequester = new CommunicationsRequester() {

            /**
             * The following is responsible for creating new messages for getting information
             * from the eANTS. It takes the UpdateRequest object and turns in into a byte array
             * message ready for transmitting
             * @param updateRequest The request for new information
             * @return if the request was successfully added
             */
            @Override
            public boolean requestUpdate(UpdateRequest updateRequest) {

                ArrayList<Byte> bytesList = new ArrayList<>();

                bytesList.add((byte)updateRequest.requestAddress());
                bytesList.add(BEGIN_BYTE);

                if (updateRequest.isStatus1())bytesList.add(STATUS1_BYTE);
                if (updateRequest.isStatus2())bytesList.add(STATUS2_BYTE);
                if (updateRequest.isStatus3())bytesList.add(STATUS3_BYTE);
                if (updateRequest.isCharge())bytesList.add(CHARGE_BYTE);
                if (updateRequest.isSpeed())bytesList.add(SPEED_BYTE);
                if (updateRequest.isOrientation())bytesList.add(ORIENTATION_BYTE);


                bytesList.add(REQUEST_DATA);
                bytesList.add(END_BYTE);
                byte[] bytes = new byte[bytesList.size()];
                for (int i = 0;i < bytes.length;i++){
                    bytes[i] = bytesList.get(i);
                }
                ComRequest comRequest = new ComRequest(bytes, false, false);

                if (isReady()){
                    arduino.addRequest(comRequest);
                    return true;
                }
                return false;
            }

            /**
             * The following is responsible for creating new control requests
             * and converting them to the byte format, ready for transmitting
             *
             * @param controlRequest The request for robot control
             * @return if the request was successfully made
             */
            @Override
            public boolean requestControl(ControlRequest controlRequest) {

                ArrayList<Byte> bytesList = new ArrayList<>();
                Robot[] robots = controlRequest.getRobots();
                for (Robot robot:robots)bytesList.add((byte)robot.getId());
                bytesList.add(BEGIN_BYTE);
                if (controlRequest.isManualEnable())bytesList.add(ENABLE_MANUAL_BYTE);
                if (controlRequest.isManualDisable())bytesList.add(DISABLE_MANUAL_BYTE);
                if (controlRequest.isCommand1())bytesList.add(COMMAND1_BYTE);
                if (controlRequest.isCommand2())bytesList.add(COMMAND2_BYTE);
                if (controlRequest.isCommand3())bytesList.add(COMMAND3_BYTE);
                if (controlRequest.isSense1Enabled()){
                    bytesList.add(SENSE1_BYTE);
                    bytesList.add((byte)controlRequest.getSense1().getId());
                }
                if (controlRequest.isSense2Enabled()){
                    bytesList.add(SENSE2_BYTE);
                    bytesList.add((byte)controlRequest.getSense2());
                }
                if (controlRequest.isSense3Enabled()){
                    bytesList.add(SENSE3_BYTE);
                    bytesList.add((byte)controlRequest.getSense3());
                }
                if (controlRequest.isForward()){
                    bytesList.add(MOVE_FORWARDS_BYTE);
                    bytesList.add((byte)controlRequest.getSpeed());
                }else if (controlRequest.isBackward()){
                    bytesList.add(MOVE_BACKWARDS_BYTE);
                    bytesList.add((byte)controlRequest.getSpeed());
                }else if (controlRequest.isRotateRight()){
                    bytesList.add(ROTATE_RIGHT_BYTE);
                    bytesList.add((byte)controlRequest.getRotateRightAngle());
                }else if (controlRequest.isRotateLeft()){
                    bytesList.add(ROTATE_LEFT_BYTE);
                    bytesList.add((byte)controlRequest.getRotateLeftAngle());
                }
                bytesList.add(END_BYTE);
                byte[] bytes = new byte[bytesList.size()];
                for (int i = 0;i < bytes.length;i++){
                    bytes[i] = bytesList.get(i);
                    //System.out.println(bytes[i]);
                }
                ComRequest comRequest = new ComRequest(bytes, false, false);
                if (isReady()){
                    arduino.addRequest(comRequest);
                    return true;
                }
                return false;
            }

            /**
             *
             * @param programRequest The request
             * @return
             */
            @Override
            public boolean requestProgram(ProgramRequest programRequest) {
                byte[] bytesArray = programRequest.getBytes();
                ArrayList<Byte> bytesList = new ArrayList<>();
                for (Robot robot:programRequest.getRobots())bytesList.add((byte)robot.getId());
                bytesList.add(BEGIN_BYTE);
                for (int i = 0;i < 100;i++)bytesList.add(BOOTLOADER_BYTE);
                for (int i = 0;i < 1;i++)bytesList.add(END_BYTE);
                for (byte b:bytesArray)bytesList.add(b);
                bytesArray = new byte[bytesList.size()];
                for (int i = 0;i < bytesArray.length;i++){
                    bytesArray[i] = bytesList.get(i);
                }
                ComRequest comRequest = new ComRequest(bytesArray, true, false);

                arduino.addRequest(comRequest);
                return true;
            }

            @Override
            public boolean isReady() {
                if (!isProgramming && arduino.isOpen())
                    return true;
                return false;
            }

            @Override
            public boolean isProgramming() {
                return isProgramming;
            }

            @Override
            public void selectConnection(int index) {
                arduino.open(index);
            }

            @Override
            public void endConnection() {
                arduino.close();
            }

            @Override
            public String[] getPorts() {
                return arduino.enumeratePorts();
            }

            @Override
            public boolean isOpen() {
                return arduino.isOpen();
            }
        };



    }

    /**
     * Getter allows for the interface to be used to request information
     * @return the Interface CommunicationsRequester
     */
    public CommunicationsRequester getCommunicationsRequester(){
        return communicationsRequester;
    }


}
