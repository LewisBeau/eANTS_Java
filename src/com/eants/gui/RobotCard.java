package com.eants.gui;

import com.eants.communication.CommunicationsRequester;
import com.eants.robots.Robot;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by lewis on 02/09/2017.
 * This class represents a single robot card, that is displayed in the robots tab
 * It takes all its information from a robot object and displays it in a JPanel container
 */
public class RobotCard extends JPanel {


    //private final JLabel robotImage = new JLabel(robotIcon, JLabel.CENTER);

    private Robot robot;
    private CommunicationsRequester communicationsRequester;

    private final static Color DEFAULT_COLOR = new Color(222, 222, 222);
    private final static Color SELECTED_COLOR = new Color(161, 208, 243);
    private final static Color HOVER_COLOR = new Color(198, 228, 243);


    private JLabel state1Label = new JLabel();
    private JLabel state2Label = new JLabel();
    private JLabel state3Label = new JLabel();
    private JLabel chargeLabel = new JLabel();
    private JLabel speedLabel = new JLabel();
    private JLabel orientationLabel = new JLabel();


    Timer timer;

    public RobotCard(Robot robot, CommunicationsRequester communicationsRequester){


        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData();
                repaint();
                revalidate();
            }
        });
        timer.start();

        this.robot = robot;
        this.communicationsRequester = communicationsRequester;

        this.setLayout(new MigLayout("fill", "[25mm:25mm:30mm][25mm:25mm:30mm]", "[grow][shrink][shrink]"));
        this.setOpaque(true);
        this.setBackground(DEFAULT_COLOR);

        JButton controlButton = new JButton("Options");

        this.add(new JLabel("Robot ID: " + robot.getId()), "growy, wrap, align center, span2");
        try {
            String filePath = "res/markers/markers25px/4x4Marker_" + robot.getId() + ".jpg";
            JLabel imageLabel = new JLabel(new ImageIcon(ImageIO.read(new File(filePath)).
                    getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            this.add(imageLabel, "grow, wrap, span2");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //

        this.add(new JLabel("Power"), "align left");
        this.add(chargeLabel, "wrap, align right");
        this.add(new JLabel("Status 1"), "align left");
        this.add(state1Label, "wrap, align right");
        this.add(new JLabel("Status 2"), "align left");
        this.add(state2Label, "wrap, align right");
        this.add(new JLabel("Status 3"), "align left");
        this.add(state3Label, "wrap, align right");

        updateData();

        this.add(controlButton, "align center, span2");


        updateSelected();

        controlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame robotFrame = new RobotOptionsFrame(robot, communicationsRequester);
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                setSelected(!robot.isSelected());

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(robot.isSelected() ? SELECTED_COLOR : DEFAULT_COLOR);
            }
        });

    }


    /**
     * Updates the cards
     */
    private void updateSelected(){
        setSelected(robot.isSelected());
    }

    /**
     * This sets the flag for where the robot is currently selected in the GUI and then colours the
     * card apropriately
     * @param selected true=selected, false=deselected
     */
    public void setSelected(boolean selected){
        robot.setSelected(selected);
        if (selected){
            this.setBackground(SELECTED_COLOR);
        }else {
            this.setBackground(DEFAULT_COLOR);
        }
        this.repaint();
    }

    public void updateData(){
        state1Label.setText(""+robot.getState1());
        state2Label.setText(""+robot.getState2());
        state3Label.setText(""+robot.getState3());
        chargeLabel.setText(""+robot.getChargeLevel()*100 + "%");
    }




}
