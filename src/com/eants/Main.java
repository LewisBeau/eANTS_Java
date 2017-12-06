package com.eants;

import com.eants.communication.Arduino;
import com.eants.communication.AutoUpdater;
import com.eants.communication.CommunicationsController;
import com.eants.communication.CommunicationsRequester;
import com.eants.gui.*;
import com.eants.gui.Frame;
import com.eants.imagerecognition.Aruco;
import com.eants.robots.*;


/**
 * Created by lewis on 26/08/2017.
 */
public class Main {

    private Frame frame;

    public static void main(String[] args){
        new Main();
    }



    public Main(){



        RobotManager robotManager = new RobotManager();
        RobotUpdater robotUpdater = robotManager.getRobotUpdater();


        Arduino arduino = new Arduino(robotUpdater);


        CommunicationsController communicationsController = new CommunicationsController(arduino, robotUpdater);
        CommunicationsRequester communicationsRequester = communicationsController.getCommunicationsRequester();

        AutoUpdater autoUpdater = new AutoUpdater(communicationsRequester, robotManager);


        Aruco aruco = new Aruco(0, robotManager);


        frame = new Frame(communicationsRequester);

        RobotPanel robotPanel = new RobotPanel(robotManager, communicationsRequester, autoUpdater);

        frame.addTab("eANTS", robotPanel);
        frame.addTab("Console", new CommandPanel());
        frame.addTab("Video", aruco.getOutputPanel());


        arduino.setUpdateListener(frame.getUpdateListener());
        robotManager.setUpdateListener(frame.getUpdateListener());
        robotManager.setRobotPanel(robotPanel);

        frame.addNotification(new Notification("Disconnected"));

        frame.setVisible(true);


    }


}
