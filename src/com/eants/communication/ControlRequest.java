package com.eants.communication;

import com.eants.robots.Robot;

/**
 * Created by lewis on 05/09/2017.
 *
 * Represents a request to control one or more robots. Can be created by any class
 * and should be passed to the communications requester interface
 */
public class ControlRequest {

    private Robot[] robots;

    private boolean sense1Enabled = false;
    private boolean sense2Enabled = false;
    private boolean sense3Enabled = false;

    private Robot sense1;
    private int sense2;
    private int sense3;

    private boolean manualEnable;
    private boolean manualDisable;
    private boolean command1 = false;
    private boolean command2 = false;
    private boolean command3 = false;
    private boolean stop = false;
    private boolean forward = false;
    private boolean backward = false;
    private int speed = 127;
    private boolean rotateLeft = false;
    private int rotateLeftAngle = 0;
    private boolean rotateRight = false;
    private int rotateRightAngle = 0;

    public Robot[] getRobots() {
        return robots;
    }

    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }

    public void setManual(boolean manual) {
        if (manual) manualEnable = true;
        if (!manual) manualDisable = true;
    }

    public boolean isManualDisable() {
        return manualDisable;
    }

    public boolean isManualEnable() {
        return manualEnable;
    }

    public boolean isCommand1() {
        return command1;
    }

    public void setCommand1() {
        this.command1 = true;
    }

    public boolean isCommand2() {
        return command2;
    }

    public void setCommand2() {
        this.command2 = true;
    }

    public boolean isCommand3() {
        return command3;
    }

    public void setCommand3() {
        this.command3 = true;
    }

    public boolean isStop() {
        return stop;
    }

    public void requestStop() {
        this.stop = true;
    }

    public boolean isRotateLeft() {
        return rotateLeft;
    }

    public void requestRotateLeft(int angle) {
        this.rotateLeft = true;
        this.rotateLeftAngle = angle;
    }

    public int getRotateLeftAngle() {
        return rotateLeftAngle;
    }

    public boolean isRotateRight() {
        return rotateRight;
    }

    public void requestRotateRight(int angle) {
        this.rotateRight = true;
        this.rotateRightAngle = angle;
    }

    public int getRotateRightAngle() {
        return rotateRightAngle;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(int speed) {
        this.speed = speed;
        this.forward = true;
    }

    public boolean isBackward() {
        return backward;
    }

    public void setBackward(int speed) {
        this.speed = speed;
        this.backward = true;
    }

    public Robot getSense1() {
        return sense1;
    }

    public void setSense1(Robot sense1) {
        this.sense1 = sense1;
        this.sense1Enabled = true;
    }

    public int getSense2() {
        return sense2;
    }

    public void setSense2(int sense) {
        this.sense2 = sense;
        this.sense2Enabled = true;
    }


    public int getSense3() {
        return sense3;
    }

    public void setSense3(int sense) {
        this.sense3 = sense;
        this.sense3Enabled = true;
    }

    public boolean isSense1Enabled() {
        return sense1Enabled;
    }

    public boolean isSense2Enabled() {
        return sense2Enabled;
    }

    public boolean isSense3Enabled() {
        return sense3Enabled;
    }

    public int getSpeed(){
        return speed;
    }


}



