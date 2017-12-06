package com.eants.gui;

import com.eants.communication.AutoUpdater;
import com.eants.communication.CommunicationsRequester;
import com.eants.communication.UpdateRequest;
import com.eants.robots.*;
import com.eants.robots.Robot;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by lewis on 02/09/2017.
 */
public class RobotPanel extends JPanel {

    private JPanel controlBar;
    private JPanel container;


    private RobotManager robotManager;
    private AutoUpdater autoUpdater;
    private CommunicationsRequester communicationsRequester;

    public RobotPanel(RobotManager robotManager, CommunicationsRequester communicationsRequester, AutoUpdater autoUpdater) {

        //The object to manage all individuals
        this.robotManager = robotManager;

        //Object for requesting communication
        this.communicationsRequester = communicationsRequester;

        this.autoUpdater = autoUpdater;
        /**
         * This allows requests for communication to be made
         */

        /*
        Action listener for button
         */
        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("Select All")){
                    //Selects all the robots in robotmanager
                    Robot[] robots = robotManager.getRobots();
                    boolean selected = false;
                    //Check if any robots are selected
                    for (Robot robot:robots)
                        if (robot.isSelected())selected = true;
                    for (int i = 0;i < robots.length;i++){
                        robots[i].setSelected(!selected);
                        robotManager.setRobot(i, robots[i]);
                    }
                    displayRobots();
                }else if(e.getActionCommand().equals("Program")){
                  new UploadForm(robotManager, communicationsRequester);
                }else if(e.getActionCommand().equals("Remove Selected eANTS")){
                    //Remove all the robots from robotManager, which are selected
                    Robot[] robots = robotManager.getRobots();
                    for (int i = robots.length-1;i > 0;i--){
                        if(robots[i].isSelected())
                            robotManager.removeRobot(i);
                    }
                    //Deals wih some strange error I don't understand...
                    if (robotManager.getRobots().length > 0 &&
                            robotManager.getRobot(0).isSelected())robotManager.removeRobot(0);
                    displayRobots();
                }else if(e.getActionCommand().equals("Refresh")){
                    Robot[] robots = robotManager.getRobots();
                    for (Robot robot:robots){
                        if (robot.isSelected()){
                           /* UpdateRequest update = new UpdateRequest(robot.getId());
                            update.getStatus1();
                            update.getStatus2();
                            update.getStatus3();
                            update.getCharge();
                            communicationsRequester.requestUpdate(update);*/
                        }
                    }
                }else if (e.getActionCommand().equals("Options")){
                    Robot[] robots = robotManager.getRobots();
                    ArrayList<Robot> robotArrayList = new ArrayList<>();
                    for (Robot robot:robots) if (robot.isSelected())robotArrayList.add(robot);
                    Robot[] selectedRobots = new Robot[robotArrayList.size()];
                    for (int i = 0;i < robotArrayList.size();i++)selectedRobots[i] = robotArrayList.get(i);
                    RobotControlFrame robotControlFrame = new RobotControlFrame(selectedRobots, communicationsRequester);
                }else if (e.getActionCommand().equals("Auto Update")){
                    new AutoUpdateFrame(autoUpdater);
                }
            }
        };




        //Set overall panel layout
        this.setLayout(new MigLayout("fill", "[90%:100%:100%]", "[shrink][grow]"));
        //Bar for setting various parameters
        controlBar = new JPanel(new MigLayout("align left", "[shrink][4cm][shrink]", "[]"));

        /*
        Add new robot menu
         */
        JLabel label1 = new JLabel("Robot ID:  ");
        JTextField idField = new JTextField();
        JButton addRobotButton = new JButton("Add Robot");
        controlBar.add(label1);
        controlBar.add(idField, "grow");
        controlBar.add(addRobotButton);

        /*
        Program selected robots menu
         */
        JButton selectButton = new JButton("Select All");
        selectButton.addActionListener(buttonListener);
        JButton programButton = new JButton("Program");
        programButton.addActionListener(buttonListener);

        controlBar.add(selectButton);
        controlBar.add(programButton);

        /*
        Remove selected robots
         */
        JButton removeRobotsButton = new JButton("Remove Selected eANTS");
        removeRobotsButton.addActionListener(buttonListener);
        controlBar.add(removeRobotsButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(buttonListener);
        controlBar.add(refreshButton);

        JButton newGroupButton = new JButton("New Group");
        newGroupButton.addActionListener(buttonListener);
        newGroupButton.setEnabled(false);
        controlBar.add(newGroupButton);

        JButton optionsButton = new JButton("Options");
        optionsButton.addActionListener(buttonListener);
        controlBar.add(optionsButton);

        JButton autoUpdateButton = new JButton("Auto Update");
        autoUpdateButton.addActionListener(buttonListener);
        controlBar.add(autoUpdateButton);

        //Container for card layout
        //container = new JPanel(new MigLayout());
        container = new JPanel(new WrapLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        container.setBackground(Frame.BACKGROUND_COLOR_DARK);
        container.setOpaque(true);
        this.setBackground(Frame.BACKGROUND_COLOR_DARK);
        this.setOpaque(true);

        scrollPane.setBorder(BorderFactory.createLineBorder(Frame.BACKGROUND_COLOR_DARK, 0));


        this.add(controlBar, "wrap, growx");
        this.add(scrollPane, "growx, growy");



        /*
        Listens for enter presses to add new robot to list
         */


        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (robotManager.isIdValid(idField.getText())) {
                    int id = Integer.parseInt(idField.getText());
                    robotManager.addRobot(new Robot(id));
                    idField.setText("");
                    displayRobots();
                }
            }
        };
        idField.addActionListener(action);
        addRobotButton.addActionListener(action);

    }



    /**
     * Takes all the robots from the robot manager and displays them
     */
    public void displayRobots() {
        container.removeAll();
        for (Robot robot:robotManager.getRobots()){
            container.add(new RobotCard(robot, communicationsRequester));
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

}
