package com.eants.robots;

/**
 * Created by lewis on 05/09/2017.
 */


public class RobotUpdate {

    private int status1 = -1;
    private int status2 = -1;
    private int status3 = -1;
    private int charge = -1;
    private int speed = -1;
    private int orientation = -1;
    private int address;

    public RobotUpdate(int address){
        this.address = address;
    }



    public int getStatus1() {
        return status1;
    }

    public int getStatus2() {
        return status2;
    }

    public int getStatus3() {
        return status3;
    }

    public int getCharge() {
        return charge;
    }

    public int getSpeed() {
        return speed;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getAddress() {
        return address;
    }

    public void setStatus1(int status1) {
        this.status1 = status1;
    }

    public void setStatus2(int status2) {
        this.status2 = status2;
    }

    public void setStatus3(int status3) {
        this.status3 = status3;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
