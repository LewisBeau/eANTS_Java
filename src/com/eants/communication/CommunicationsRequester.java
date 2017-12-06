package com.eants.communication;

import com.eants.robots.Robot;

import java.io.File;

/**
 * Created by lewis on 03/09/2017.
 *
 * this interface is responsible for receiving updates, control requests and program
 * requests from the GUI and update scheduler and then passing these on to the communications controller,
 * which then creates the byte messages to be sent to the arduino
 *
 * The various methods allow information to be requested and sent
 *
 */
public interface CommunicationsRequester {

    /**
     * Allows requests for certain information to be made
     * @param updateRequest The request for new information
     */
    boolean requestUpdate(UpdateRequest updateRequest);

    /**
     * Allows requests for certain information to be made
     * @param controlRequest The request for robot control
     */
    boolean requestControl(ControlRequest controlRequest);

    /**
     * Requests certain robots be programmed
     * @param programRequest The request
     * @return If the programming can be currently fulfilled
     */
    boolean requestProgram(ProgramRequest programRequest);


    /**
     * Returns true if the communicator is ready to transmit and false if it is currently busy
     * @return the flag
     */
    boolean isProgramming();

    /**
     * Returns true if the communicator is ready to transmit and false if it is currently busy
     * @return the flag
     */
    boolean isReady();


    /**
     * Selects the desired connection from the availabe ports
     * @param index
     */
    void selectConnection(int index);

    /**
     * Ends the current connection
     */
    void endConnection();


    /**
     * Gets the current ports connected to computer
     * @return A string array containing the ports
     */
    String[] getPorts();

    /**
     * Tells if the arduino connection is open
     * @return
     */
    boolean isOpen();



}
