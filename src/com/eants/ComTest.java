package com.eants;

import com.eants.communication.*;
import com.eants.gui.UpdateListener;
import com.eants.robots.RobotUpdate;
import com.eants.robots.RobotUpdater;
import com.sun.org.apache.xml.internal.security.encryption.CipherData;


import javax.swing.*;
import java.util.Scanner;


/**
 * Created by lewis on 03/09/2017.
 */
public class ComTest {

    public static void main(String[] args){

        RobotUpdater robotUpdater = new RobotUpdater() {

            @Override
            public void updateState(RobotUpdate robotUpdate) {
                System.out.println("Address: " + robotUpdate.getAddress());
                if (robotUpdate.getStatus1() != -1) System.out.println("Status 1: " + robotUpdate.getStatus1());
                if (robotUpdate.getStatus2() != -1) System.out.println("Status 2: " + robotUpdate.getStatus2());
                if (robotUpdate.getStatus3() != -1) System.out.println("Status 3: " + robotUpdate.getStatus3());
                if (robotUpdate.getStatus1() != -1) System.out.println("Charge level: " + robotUpdate.getCharge());
                if (robotUpdate.getStatus1() != -1) System.out.println("Speed: " + robotUpdate.getSpeed());
                if (robotUpdate.getStatus1() != -1) System.out.println("Orientation: " + robotUpdate.getOrientation());
            }

            @Override
            public void updateProgramProgress(float f) {

            }
        };

        Arduino arduino = new Arduino(robotUpdater);
        arduino.setUpdateListener(new UpdateListener() {
            @Override
            public void connectedStateChange(int state) {
                //bla bla bla
            }

            @Override
            public void queueLength(int length) {

            }

            @Override
            public void isProgramming(boolean isProgramming) {

            }

            @Override
            public void programmingProgress(double progress) {

            }
        });




        String[] ports = arduino.enumeratePorts();
        for (String string:ports){
            System.out.println(string);

        }

        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();

        arduino.open(index);

        CommunicationsController communicationsController = new CommunicationsController(arduino, robotUpdater);
        CommunicationsRequester communicationsRequester = communicationsController.getCommunicationsRequester();
        UpdateRequest updateRequest = new UpdateRequest(1);
        //updateRequest.getStatus1();
        communicationsRequester.requestUpdate(updateRequest);

        /*
        ComRequest comRequest = new ComRequest(new byte[]{
                1,
                CommunicationsController.BEGIN_BYTE,
                CommunicationsController.STATUS1_BYTE,
                CommunicationsController.REQUEST_DATA}, false);

        arduino.addRequest(comRequest);
        */





    }

}
