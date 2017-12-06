package com.eants.imagerecognition;

import com.eants.gui.*;
import com.eants.gui.Frame;
import com.eants.robots.Robot;
import com.eants.robots.RobotManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;


import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;


/**
 * Created by lewis on 12/09/2017.
 */
public class ArucoPanel extends JPanel{

    private DisplayPanel displayPanel;
    private boolean overlayEnabled = false;

    private JTextField channelField;
    JButton openButton;

    OpenListener openListener;

    RobotManager robotManager;

    public ArucoPanel(OpenListener openListener, RobotManager robotManager) {

        this.openListener = openListener;
        this.robotManager = robotManager;

        ActionListener actionListener = e -> {
            if (e.getActionCommand().equals("Feed")){
                overlayEnabled = false;
            }else if (e.getActionCommand().equals("Overlay")){
                overlayEnabled = true;
            }else if (e.getActionCommand().equals("Open")){
                if (openListener.openChannel(Integer.parseInt(channelField.getText()))){
                    //Enable everything
                }
            }else if ((e.getActionCommand().equals("Add scanned"))){
                robotManager.addFoundRobots();
            }
        };

        this.setLayout(new MigLayout("fill", "[grow]", "[shrink]4mm[grow]"));

        JPanel controlBar = new JPanel(new MigLayout("align left", "[][20mm]", "[shrink]"));
        displayPanel = new DisplayPanel();

        JLabel label1 = new JLabel("Channel:");
        channelField = new JTextField("0");
        openButton = new JButton("Open");
        openButton.addActionListener(actionListener);

        JRadioButton imageRadioOption = new JRadioButton("Feed");
        JRadioButton videoRadioOption = new JRadioButton("Overlay");
        ButtonGroup feedRadioGroup = new ButtonGroup();
        feedRadioGroup.add(imageRadioOption);
        feedRadioGroup.add(videoRadioOption);
        imageRadioOption.setSelected(true);
        imageRadioOption.addActionListener(actionListener);
        videoRadioOption.addActionListener(actionListener);

        JButton autoAddButton = new JButton("Add scanned");
        autoAddButton.addActionListener(actionListener);

        controlBar.add(label1);
        controlBar.add(channelField, "growx");
        controlBar.add(openButton);

        controlBar.add(imageRadioOption);
        controlBar.add(videoRadioOption);

        controlBar.add(autoAddButton);

        //controlBar.setBackground(Frame.ELEMENT_LIGHT_COLOR);
        //controlBar.setOpaque(true);

        this.setBackground(Frame.BACKGROUND_COLOR_DARK);
        this.setOpaque(true);

        this.add(controlBar, "wrap, growx, align left");
        this.add(displayPanel, "growx, growy");

    }


    void setVideoImage(BufferedImage videoImage){
        if (!overlayEnabled){
            displayPanel.setImage(videoImage);
        }
    }


    void setOutputImage(BufferedImage outputImage){
        if (overlayEnabled) {
            displayPanel.setImage(outputImage);
        }
    }


    void setOpenEnable(boolean openEnable){
        openButton.setEnabled(openEnable);
    }


    private class DisplayPanel extends JPanel{

        BufferedImage image;

        private double imageScale = 1;

        private MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (image != null){
                    int x = e.getX();
                    int y = e.getY();

                    int[] perimeters = getImagePerimeters();
                    x = x - perimeters[0];
                    imageScale = ((double)perimeters[2])/image.getWidth();
                    x = (int)(x / imageScale);
                    y = (int)(y / imageScale);

                    Robot[] robots = robotManager.getRobots();

                    double scaler = RobotManager.ROBOT_SCALE_MULITPLIER*imageScale;
                    /*System.out.println("x: " + x + " y: " + y);
                    System.out.println("x: " + robotManager.getRobotById(2).getxPos() + " y: " + robotManager.getRobotById(2).getyPos());
                    System.out.println(robotManager.getRobotById(2).getScale());*/

                    for (Robot robot : robots){
                        if (robot.getxPos() > (x-robot.getScale()*scaler) && robot.getxPos() < (x+robot.getScale()*scaler)){
                            if (robot.getyPos() > (y-robot.getScale()*scaler) && robot.getyPos() < (y+robot.getScale()*scaler)){
                                robot.setSelected(!robot.isSelected());
                                robotManager.setRobotByid(robot);
                                robotManager.displayRobots();
                            }
                        }
                    }
                }


            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };


        public DisplayPanel(){
            this.addMouseListener(mouseListener);
        }

        void setImage(BufferedImage image){
            this.image = image;
        }

        int[] getImagePerimeters(){
            int xOffset = -1;
            int yOffset = -1;
            int width = -1;
            int height = -1;

            if (image!= null){
                double imageAspect = (double)image.getHeight()/image.getWidth();
                double frameAspect = (double)this.getHeight()/this.getWidth();
                if (frameAspect > imageAspect) {
                    xOffset = 0;
                    yOffset = 0;
                    width = this.getWidth();
                    height = (int)(this.getWidth()*imageAspect);
                }else if (frameAspect < imageAspect){
                    xOffset = (this.getWidth()-((int)(this.getHeight()/imageAspect)))/2;
                    yOffset = 0;
                    width = (int)(this.getHeight()/imageAspect);
                    height = this.getHeight();
                }else {
                    xOffset = 0;
                    yOffset = 0;
                    width = this.getHeight();
                    height = this.getWidth();
                }
            }
            return new int[]{xOffset, yOffset, width, height};
        }

        @Override
        public void paintComponent(Graphics g){
            g.setColor(Frame.BACKGROUND_COLOR_DARK);
            g.fillRect(0,0,this.getWidth(), this.getHeight());
            if (image!= null){

                int[] imagePerimeters = getImagePerimeters();
                g.drawImage(image, imagePerimeters[0], imagePerimeters[1], imagePerimeters[2],imagePerimeters[3], this);
                //Draw circles showing selected robots
                if (!overlayEnabled){
                    int imageOffset = imagePerimeters[0];
                    int imageWidth = imagePerimeters[2];
                    int imageHeight = imagePerimeters[3];
                    for (Robot robot:robotManager.getRobots()){
                        if(robot.getxPos() != -1 && robot.getyPos() != -1){

                            double charge = robot.getChargeLevel();
                            if (charge >.8){
                                g.setColor(new Color(102, 187, 106));
                            }else if (charge > .5){
                                g.setColor(new Color(153, 187, 103));
                            }else if (charge > .3){
                                g.setColor(new Color(187, 173, 87));
                            }else if (charge < .3){
                                g.setColor(new Color(187, 73, 68));
                            }

                            int centreX = getRobotCentreLocationX(robot.getxPos(), imageWidth, imageOffset);
                            int centreY = getRobotCentreLocationY(robot.getyPos(), imageHeight);
                            int diameter = getRobotWidth(robot.getScale());
                            g.fillArc(centreX-diameter/2, centreY-diameter/2, diameter, diameter, 0, 360);

                            g.setColor(new Color(0,0,0));
                            g.drawString("Robot: " + robot.getId(), centreX, centreY);
                            g.drawString("State 1: " + robot.getState1(), centreX, centreY+20);
                            g.drawString("State 2: " + robot.getState2(), centreX, centreY+40);
                            g.drawString("State 3: " + robot.getState3(), centreX, centreY+60);

                        }
                    }
                }
            }
        }

        private int getRobotCentreLocationX(int x, int imageWidth, int imageOffset){
            return imageOffset+x*imageWidth/image.getWidth();
        }

        private int getRobotCentreLocationY(int y, int imageHeight){
            return y*imageHeight/image.getHeight();
        }

        private int getRobotWidth(int robotScale){
            return (int)(RobotManager.ROBOT_SCALE_MULITPLIER*imageScale*robotScale);
        }



    }
}
