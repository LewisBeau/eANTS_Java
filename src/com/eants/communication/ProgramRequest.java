package com.eants.communication;

import com.eants.robots.Robot;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by lewis on 05/09/2017.
 *
 * Represents a new request to program a robot group, individual or all robots
 */
public class ProgramRequest {


    private Robot[] robots;
    private File hexFile;


    private static final int START_ADDRESS = 0;
    private static final int COUNT_ADDRESS = 1;
    private static final int COUNT_LENGTH = 2;
    //private static final int ADDRESS_ADDRESS = 3;
    //private static final int ADDRESS_LENGTH = 4;
    private static final int RECORD_ADDRESS = 7;
    private static final int RECORD_LENGTH = 2;
    private static final int DATA_ADDRESS = 7;
    //private static final int CHECKSUM_LENGTH = 2;

    private static final int DATA_TYPE = 16; //Hex


    public ProgramRequest(Robot[] robots, File hexFile){
        this.robots = robots;
        this.hexFile = hexFile;
    }


    /**
     * Deced the intel hex file
     * @return The byte array
     */

    byte[] getBytes(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(hexFile));
            String line = bufferedReader.readLine();
            ArrayList<Byte> bytes = new ArrayList<>();
            while (line != null){
                byte[] lineBytes = getLineData(line);
                if (lineBytes != null){ //Not the last line, or not not encoding data
                    for (byte byteAt: lineBytes){
                        bytes.add(byteAt);
                    }
                }
                line = bufferedReader.readLine();
            }
            byte[] bytesArray = new byte[bytes.size()];
            for (int i = 0;i < bytes.size();i++)bytesArray[i] = bytes.get(i);
            return bytesArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    byte[] getLineData(String line){
        if (line.charAt(START_ADDRESS) == ':'){
            String byteLengthString = line.substring(COUNT_ADDRESS,COUNT_ADDRESS+COUNT_LENGTH);
            int length = Integer.parseInt(byteLengthString, DATA_TYPE);
            byte[] bytes = new byte[length];
            if (line.substring(RECORD_ADDRESS,RECORD_ADDRESS+RECORD_LENGTH).equals("00")){ //Normal data
                for (int i = 0;i < length;i++){
                    bytes[i] = (byte)Integer.parseInt(line.substring(DATA_ADDRESS+i*2, DATA_ADDRESS+2+i*2), DATA_TYPE);
                }
                return bytes;
            }
        }
        return null;
    }


    Robot[] getRobots(){
        return robots;
    }

}
