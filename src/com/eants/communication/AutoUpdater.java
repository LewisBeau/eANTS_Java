package com.eants.communication;

import com.eants.robots.Robot;
import com.eants.robots.RobotManager;

import javax.swing.*;


/**
 * Created by lewis on 18/09/2017.
 */
public class AutoUpdater {

    private RobotManager robotManager;
    private CommunicationsRequester communicationsRequester;

    private boolean status1 = false;
    private boolean status2 = false;
    private boolean status3 = false;
    private boolean charge = false;
    private boolean speed = false;
    private boolean orientation = false;

    private boolean sense1 = false;
    private boolean sense2 = false;
    private boolean sense3 = false;

    private int frequency = 5000;

    Timer timer;

    public AutoUpdater(CommunicationsRequester communicationsRequester, RobotManager robotManager){
        this.robotManager = robotManager;
        this.communicationsRequester = communicationsRequester;

        timer = new Timer(frequency, e -> {
            for (Robot robot:robotManager.getRobots()){
                UpdateRequest updateRequest = new UpdateRequest(robot.getId());
                ControlRequest controlRequest = new ControlRequest();
                controlRequest.setRobots(new Robot[]{robot});
                if (status1)updateRequest.requestStatus1();
                if (status2)updateRequest.requestStatus2();
                if (status3)updateRequest.requestStatus3();
                if (charge)updateRequest.requestCharge();
                if (speed)updateRequest.requestSpeed();
                if (orientation)updateRequest.getOrientation();
                if (sense1)controlRequest.setSense1(robot.getClosestRobot());
                if (sense2)controlRequest.setSense2(robot.getSense2());
                if (sense3)controlRequest.setSense3(robot.getSense3());

                communicationsRequester.requestUpdate(updateRequest);
                communicationsRequester.requestControl(controlRequest);
            }
        });

    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    public void setEnabled(boolean enabled){
        if (enabled){
            if (!timer.isRunning())timer.start();
        }else {
            if (timer.isRunning())timer.stop();
        }
    }

    public void setStatus1(boolean status1) {
        this.status1 = status1;
    }

    public void setStatus2(boolean status2) {
        this.status2 = status2;
    }

    public void setStatus3(boolean status3) {
        this.status3 = status3;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public void setSpeed(boolean speed) {
        this.speed = speed;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public void setSense1(boolean sense1){
        this.sense1 = sense1;
    }

    public void setSense2(boolean sense2){
        this.sense2 = sense2;
    }

    public void setSense3(boolean sense3){
        this.sense3 = sense3;
    }
}
