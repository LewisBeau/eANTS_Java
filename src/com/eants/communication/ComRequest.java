package com.eants.communication;

/**
 * Created by lewis on 04/09/2017.
 *
 * This class represents a new message being sent to the arduino to be passed on
 * It basically just contains a string of bytes, which can be information requests, control
 * requests or programming requests
 *
 *
 *
 */
public class ComRequest {


    //The byte array to be sent
    private byte[] bytes;
    //If this is a programming request
    private boolean programRequest;
    //The number of requests requiring a response in this message
    private int requestNumber = 0;
    //The id of the robot being addressed
    //Default:all robots
    private int id = 201;

    public boolean isImportant() {
        return important;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public boolean isProgramRequest() {
        return programRequest;
    }

    private boolean important;

    /**
     * Represents a command to be sent a instruction to the eANT
     * @param bytes the bytes
     * @param programRequest if it is a programming job
     * @param important if it is important, programming jobs are always important
     */


    public ComRequest(byte[] bytes, boolean programRequest, boolean important){
        this.bytes = bytes;
        this.programRequest = programRequest;
        this.important = important;


        //Find out the number of requests in the message, only applied to information
        //requests
        for (byte message:bytes){
            if (message == CommunicationsController.STATUS1_BYTE||
                    message == CommunicationsController.STATUS2_BYTE||
                    message == CommunicationsController.STATUS3_BYTE||
                    message == CommunicationsController.CHARGE_BYTE||
                    message == CommunicationsController.SPEED_BYTE||
                    message == CommunicationsController.ORIENTATION_BYTE){
                requestNumber++;
            }
            //Find out the robot id
            int byteId = message>0 ? message : 256+message;  //Convert to unsigned value
            if (byteId <= 200){
                id = byteId;
            }
        }

    }

    public ComRequest(byte[] bytes, boolean programRequest){
        this(bytes, programRequest, false);
    }

}

