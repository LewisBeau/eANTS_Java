package com.eants.gui;

import com.eants.communication.CommunicationsRequester;
import com.eants.communication.ControlRequest;
import com.eants.robots.Robot;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import java.awt.event.ActionListener;


/**
 * Created by lewis on 18/09/2017.
 */
public class RobotControlPanel extends JPanel{

    private Robot[] robots;
    private CommunicationsRequester communicationsRequester;



    private JTextField sense1Field = new JTextField("0");
    private JTextField sense2Field = new JTextField("0");
    private JTextField sense3Field = new JTextField("0");


    private JCheckBox manualBox = new JCheckBox("Enable manual mode");
    private JCheckBox sense1Box = new JCheckBox("Set");
    private JCheckBox sense2Box = new JCheckBox("Set");
    private JCheckBox sense3Box = new JCheckBox("Set");
    private JCheckBox command1Box = new JCheckBox("CMD 1");
    private JCheckBox command2Box = new JCheckBox("CMD 2");
    private JCheckBox command3Box = new JCheckBox("CMD 3");


    public RobotControlPanel(Robot[] robots, CommunicationsRequester communicationsRequester){
        this.robots = robots;
        this.communicationsRequester = communicationsRequester;

        JButton writeButton = new JButton("Write");
        writeButton.addActionListener(e -> {
            ControlRequest controlRequest = new ControlRequest();
            controlRequest.setRobots(robots);
            controlRequest.setManual(manualBox.isSelected());
            //if (sense1Box.isSelected())controlRequest.setSense1(Integer.parseInt(sense1Field.getText()));
            if (sense2Box.isSelected())controlRequest.setSense2(Integer.parseInt(sense2Field.getText()));
            if (sense3Box.isSelected())controlRequest.setSense3(Integer.parseInt(sense3Field.getText()));
            if (command1Box.isSelected())controlRequest.setCommand1();
            if (command2Box.isSelected())controlRequest.setCommand2();
            if (command3Box.isSelected())controlRequest.setCommand3();
            communicationsRequester.requestControl(controlRequest);
        });

        sense1Box.setEnabled(false);
        sense1Field.setEnabled(false);

        //JPanel infoSetterPanel = new JPanel(new MigLayout("fill", "[shrink][30mm][]"));
        //infoSetterPanel.add(new JLabel("write"), "span 3, align right, wrap");
        this.setLayout(new MigLayout("fill", "[shrink][30mm][]", "[]"));

        this.add(manualBox, "wrap, span 3");
        this.add(new JLabel("Sense 1: "));
        this.add(sense1Field, "growx");
        this.add(sense1Box, "wrap");
        this.add(new JLabel("Sense 3: "));
        this.add(sense2Field, "growx");
        this.add(sense2Box, "wrap");
        this.add(new JLabel("Sense 2: "));
        this.add(sense3Field, "growx");
        this.add(sense3Box, "wrap");
        this.add(writeButton);
        this.add(command1Box, "");
        this.add(command2Box, "");
        this.add(command3Box, "wrap");
        this.add(writeButton, "span 3, wrap, align left");


        JTextField rotateAngleLabel = new JTextField("90");
        JTextField speedLabel = new JTextField("127");
        ActionListener actionListener = e -> {
            ControlRequest controlRequest = new ControlRequest();
            controlRequest.setRobots(robots);
            if (e.getActionCommand().equals("Forward")){
                controlRequest.setForward(Integer.parseInt(speedLabel.getText()));
            }else if (e.getActionCommand().equals("Backward")){
                controlRequest.setBackward(Integer.parseInt(speedLabel.getText()));
            }else if (e.getActionCommand().equals("Right")){
                controlRequest.requestRotateRight(Integer.parseInt(rotateAngleLabel.getText()));
            }else if (e.getActionCommand().equals("Left")){
                controlRequest.requestRotateLeft(Integer.parseInt(rotateAngleLabel.getText()));
            }else if (e.getActionCommand().equals("Left")){
                controlRequest.requestStop();
            }
            communicationsRequester.requestControl(controlRequest);
        };

        JPanel movePanel = new JPanel(new MigLayout("fill", "[][][]", "[][][][]"));

        JButton forwardButton = new JButton("Forward");
        forwardButton.addActionListener(actionListener);
        JButton backwardButton = new JButton("Backward");
        backwardButton.addActionListener(actionListener);
        JButton rightButton = new JButton("Right");
        rightButton.addActionListener(actionListener);
        JButton leftButton = new JButton("Left");
        leftButton.addActionListener(actionListener);
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(actionListener);


        movePanel.add(forwardButton, "span 3, align center, wrap");
        movePanel.add(leftButton, "align right");
        movePanel.add(stopButton, "");
        movePanel.add(rightButton, "wrap, align left");
        movePanel.add(backwardButton, "span 3, align center, wrap");
        movePanel.add(new JLabel("Angle: "));
        movePanel.add(rotateAngleLabel, "wrap, grow x");
        movePanel.add(new JLabel("Speed: "));
        movePanel.add(speedLabel, "wrap, grow x");

        this.add(movePanel, "span 3, align center");


    }

}
