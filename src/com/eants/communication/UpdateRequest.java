package com.eants.communication;

import com.eants.robots.Robot;

/**
 * Created by lewis on 05/09/2017.
 */
public class UpdateRequest {


    private boolean status1 = false;
    private boolean status2 = false;
    private boolean status3 = false;


    private boolean charge = false;
    private boolean speed = false;
    private boolean orientation = false;
    private int address;


    public UpdateRequest(int id){
        this.address = id;
    }

    public boolean isStatus1() {
        return status1;
    }

    public void requestStatus1() {
        this.status1 = true;
    }

    public boolean isStatus2() {
        return status2;
    }

    public void requestStatus2() {
        this.status2 = true;
    }

    public boolean isStatus3() {
        return status3;
    }

    public void requestStatus3() {
        this.status3 = true;
    }

    public boolean isCharge() {
        return charge;
    }

    public void requestCharge() {
        this.charge = true;
    }

    public boolean isSpeed() {
        return speed;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public void requestSpeed() {
        this.speed = true;
    }

    public void getOrientation() {
        this.orientation = true;
    }

    public int requestAddress() {
        return address;
    }

}
