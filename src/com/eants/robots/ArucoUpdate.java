package com.eants.robots;

/**
 * Created by lewis on 13/09/2017.
 */
public class ArucoUpdate {

    private int address;
    private int xPos;
    private int yPos;
    private int scale;
    private int orientation;

    public ArucoUpdate(int address, int xPos, int yPos, int scale, int orientation){
        this.address = address;
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = scale;
        this.orientation = orientation;
    }

    public int getAddress(){
        return address;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getScale() {
        return scale;
    }

    public int getOrientation() {
        return orientation;
    }


}
