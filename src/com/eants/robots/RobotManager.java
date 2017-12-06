package com.eants.robots;

import com.eants.gui.RobotPanel;
import com.eants.gui.UpdateListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lewis on 02/09/2017.
 *
 * This class is responsible for managing all the individual robots. It
 */


public class RobotManager {


    /**
     * The arraylist containing all the current sorted robots
     */
    private ArrayList<Robot> robots = new ArrayList<>();

    private ArrayList<Integer> foundRobots = new ArrayList<>();

    private UpdateListener updateListener;

    private RobotPanel robotPanel;

    public static final double ROBOT_SCALE_MULITPLIER = 2;

    public RobotManager(){

        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Robot[] robotsArray = getRobots();
                for (Robot robot:robotsArray){
                    double closestValue = 99999;
                    for(Robot otherRobot:robotsArray){
                        int x1 = robot.getxPos();
                        int y1 = robot.getyPos();
                        int x2 = otherRobot.getxPos();
                        int y2 = otherRobot.getyPos();
                        if (x1!=-1 && x2 != -1){
                            double distance = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
                            if (distance < closestValue){
                                robot.setClosestRobot(otherRobot);
                                closestValue = distance;
                            }
                        }
                    }
                    //if (robot.getClosestRobot()!= null)System.out.println(robot.getClosestRobot().getId());
                }
            }
        });
        timer.start();

    }

    public void setUpdateListener(UpdateListener updateListener){
        this.updateListener = updateListener;
    }

    /**
     * Getter for the robot updater interface. This should be passed to the Communicationscontroller
     * @return The RobotUpdater interface
     */
    public RobotUpdater getRobotUpdater(){
        return new RobotUpdater() {
            @Override
            public void updateState(RobotUpdate robotUpdate) {
                /*
                System.out.println("Address: " + robotUpdate.getAddress());
                if (robotUpdate.getStatus1() != -1) System.out.println("Status 1: " + robotUpdate.getStatus1());
                if (robotUpdate.getStatus2() != -1) System.out.println("Status 2: " + robotUpdate.getStatus2());
                if (robotUpdate.getStatus3() != -1) System.out.println("Status 3: " + robotUpdate.getStatus3());
                if (robotUpdate.getStatus1() != -1) System.out.println("Charge level: " + robotUpdate.getCharge());
                if (robotUpdate.getStatus1() != -1) System.out.println("Speed: " + robotUpdate.getSpeed());
                if (robotUpdate.getStatus1() != -1) System.out.println("Orientation: " + robotUpdate.getOrientation());
                */
                Robot updatedRobot = getRobotById(robotUpdate.getAddress());
                if (robotUpdate.getStatus1() != -1) updatedRobot.setState1(robotUpdate.getStatus1());
                if (robotUpdate.getStatus2() != -1) updatedRobot.setState2(robotUpdate.getStatus2());
                if (robotUpdate.getStatus3() != -1) updatedRobot.setState3(robotUpdate.getStatus3());
                if (robotUpdate.getCharge() != -1) updatedRobot.setChargeLevel((double)robotUpdate.getCharge()/255);
                if (robotUpdate.getSpeed() != -1) updatedRobot.setSpeed((double)robotUpdate.getSpeed()/255);
                if (robotUpdate.getOrientation() != -1) updatedRobot.setOrientation(((double)robotUpdate.getOrientation()/255)*360);
            }
            @Override
            public void updateProgramProgress(float progress) {

            }
        };
    }

    public void setRobotPanel(RobotPanel robotPanel){
        this.robotPanel = robotPanel;
    }


    public void addFoundRobots(){
        for (int address:foundRobots)addRobot(new Robot(address));
        foundRobots.clear();
        if (robotPanel!= null)robotPanel.displayRobots();
    }

    public void updateState(ArucoUpdate arucoUpdate){
        Robot robot = getRobotById(arucoUpdate.getAddress());
        if (robot != null){
            robot.setxPos(arucoUpdate.getxPos());
            robot.setyPos(arucoUpdate.getyPos());
            robot.setArucoOrientation(arucoUpdate.getOrientation());
            robot.setScale(arucoUpdate.getScale());

            setRobotByid(robot);

        }else {
            if (!foundRobots.contains(arucoUpdate.getAddress()))
                foundRobots.add(arucoUpdate.getAddress());
        }
    }

    /**
     * Adds a robot to the end of the list and then sorts the list by robot id
     * @param robot the robot to be added
     */
    public void addRobot(Robot robot){
        robots.add(robot);
        Collections.sort(robots, (robot1, robot2) -> (robot1.getId() < (robot2.getId())) ? -1 : 1);
    }

    /**
     * Sets a robot at a given index
     * @param index The robot's index
     * @param robot The robots to be set
     */
    public void setRobot(int index, Robot robot){
        robots.set(index, robot);
    }


    /**
     * Gets a specific robot
     * @param index the index of the robot
     * @return the robot
     */
    public Robot getRobot(int index){
        return robots.get(index);
    }

    /**
     * Removes a specific robot
     * @param index
     */
    public void removeRobot(int index){
        robots.remove(index);
    }

    /**
     * Gets an array of all the robots
     * @return The array of robots
     */
    public Robot[] getRobots(){
        Robot[] robotsArray = new Robot[robots.size()];
        for (int i = 0;i < robotsArray.length;i++){
            robotsArray[i] = robots.get(i);
        }
        return robotsArray;
    }

    /**
     * Returns a specific robot by id
     * @param id the id reuquired
     * @return the found robot
     */
    public Robot getRobotById(int id){
        for (Robot robot:robots){
            if (robot.getId() == id){
                return robot;
            }
        }
        return null;
    }

    public void setRobotByid(Robot robot){
        for (int i = 0;i < robots.size();i++){
            if (robots.get(i).getId() == robot.getId()){
                robots.set(i, robot);
                break;
            }
        }
    }

    public void displayRobots(){
        robotPanel.displayRobots();
    }


    /**
     * Checks if a new id is valid and therefore not a duplicate of of incorrect format
     * @param string the id supplied, should be an integer value
     * @return if the value is valid
     */
    public boolean isIdValid(String string){
        try{
            Integer value = Integer.parseInt(string);
            for (Robot robot : robots)
                if (robot.getId() == value)return false;
            return true;
        }catch (NumberFormatException e){
            System.out.println("Incorrect format");
            return false;
        }
    }

    /**
     * Gets the currently selected robots
     * @return the robots tagged as selected
     */
    public Robot[] getSelectedRobots(){

        ArrayList<Robot> selectedRobots = new ArrayList<>();

        for (Robot robot:robots){
            if (robot.isSelected())
                selectedRobots.add(robot);
        }
        Robot[] robots = new Robot[selectedRobots.size()];
        for (int i = 0;i < robots.length;i++){
            robots[i] = selectedRobots.get(i);
        }
        return robots;
    }







}
