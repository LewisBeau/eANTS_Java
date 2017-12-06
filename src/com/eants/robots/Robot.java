package com.eants.robots;

/**
 * Created by lewis on 02/09/2017.
 *
 * This class represents an individual robot. It contains all the information on an
 * individual robot and organises communication, programming and state reading
 */



public class Robot {



    /**
     * The robots current battery level (0-1) in percent
     */
    private double chargeLevel;


    /**
     * The robots current forward speed, in percent ((-1)-(+1))
     * And the robots current angle(0-359)
     */
    private double speed;
    private double orientation;

    /**
     * The robots unique identifier
     */
    private int id;

    /**
     * The current robots states
     */
    private int state1 = -1;
    private int state2 = -1;
    private int state3 = -1;
    //If the robot is in manual mode/paused
    private boolean manualEnabled;

    /**
     * The closest robot
     */
    private Robot closestRobot;
    private int sense2 = -1;
    private int sense3 = -1;

    /**
     * Defaults
     */
    private final static int DEFAULT_SPEED = 0;
    private final static int DEFAULT_ORIENTATION = 0;
    private final static int DEFAULT_CHARGE_LEVEL = 1;

    //Robot removed after not being detected for 1.5s
    private final static long MOVEMENT_TIMEOUT = 2000;
    //Status and other data timeout

    private long lastArucoUpdateTime = 0;
    /**
     *UI variable
     */
    private boolean selected = false;

    /**
     * The following variables are retrieved from the aruco monitor
     */

    private int xPos;
    private int yPos;
    private int scale;
    private int arucoOrientation;

    /**
     * Creates a default robot at selected id
     * @param id
     */
    public Robot(int id){
        this.id = id;
        speed = DEFAULT_SPEED;
        orientation = DEFAULT_ORIENTATION;
        chargeLevel = DEFAULT_CHARGE_LEVEL;

    }



    /**
     * Getters and setters
     */

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getChargeLevel() {
        return chargeLevel;
    }

    public void setChargeLevel(double chargeLevel) {
        this.chargeLevel = chargeLevel;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public int getState1() {
        return state1;
    }

    public void setState1(int state1) {
        this.state1 = state1;
    }

    public int getState2() {
        return state2;
    }

    public void setState2(int state2) {
        this.state2 = state2;
    }

    public int getState3() {
        return state3;
    }

    public void setState3(int state3) {
        this.state3 = state3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArucoOrientation() {
        return arucoOrientation;
    }

    public void setArucoOrientation(int arucoOrientation) {
        this.arucoOrientation = arucoOrientation;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getyPos() {
        if (System.currentTimeMillis()-lastArucoUpdateTime > MOVEMENT_TIMEOUT){
            yPos = -1;
        }
        return yPos;
    }

    public void setyPos(int yPos) {
        lastArucoUpdateTime = System.currentTimeMillis();
        this.yPos = yPos;
    }

    public int getxPos() {
        if (System.currentTimeMillis()-lastArucoUpdateTime > MOVEMENT_TIMEOUT){
            xPos = -1;
        }
        return xPos;
    }

    public void setxPos(int xPos) {
        lastArucoUpdateTime = System.currentTimeMillis();
        this.xPos = xPos;
    }

    public void setManualEnabled(boolean value){
        this.manualEnabled = value;
    }

    public void setClosestRobot(Robot robot){
        lastArucoUpdateTime = System.currentTimeMillis();
        this.closestRobot = robot;
    }

    public Robot getClosestRobot(){
        if (System.currentTimeMillis()-lastArucoUpdateTime > MOVEMENT_TIMEOUT){
            if (closestRobot!= null)return closestRobot;
        }
        return null;
    }

    public int getSense2() {
        return sense2;
    }

    public void setSense2(int sense2) {
        this.sense2 = sense2;
    }

    public int getSense3() {
        return sense3;
    }

    public void setSense3(int sense3) {
        this.sense3 = sense3;
    }

    /**
     *UI stuff
     */

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }




}
