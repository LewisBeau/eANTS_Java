package com.eants.gui;

import com.eants.communication.CommunicationsRequester;
import com.eants.communication.UpdateRequest;
import com.eants.robots.Robot;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by lewis on 18/09/2017.
 */

public class RobotOptionsFrame extends JFrame {

    private Robot robot;
    private CommunicationsRequester communicationsRequester;


    private JLabel state1Label = new JLabel();
    private JLabel state2Label = new JLabel();
    private JLabel state3Label = new JLabel();
    private JLabel chargeLabel = new JLabel();
    private JLabel speedLabel = new JLabel();
    private JLabel orientationLabel = new JLabel();





    public RobotOptionsFrame(Robot robot, CommunicationsRequester communicationsRequester){

        this.robot = robot;
        this.communicationsRequester = communicationsRequester;

        setupEscapeClose();

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Refresh")){
                    UpdateRequest updateRequest = new UpdateRequest(robot.getId());
                    updateRequest.requestStatus1();
                    updateRequest.requestStatus2();
                    updateRequest.requestStatus3();
                    updateRequest.requestCharge();
                    communicationsRequester.requestUpdate(updateRequest);
                }
            }
        };

        this.setTitle("Robot " + robot.getId());

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setSize(dimension.width / 5, (dimension.width / 10) * 3);
        this.setLocation(dimension.width / 2, dimension.height / 3);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon imageIcon = new ImageIcon("res/eantsicon.png");
        this.setIconImage(imageIcon.getImage());

        this.setLayout(new MigLayout("fill", "[fill, grow]", "[]"));


        Timer timer = new Timer(1000, e -> {
            updateInfo();
            repaint();
        });
        JPanel infoPanelContainer = new JPanel(new MigLayout("fill", "[][fill, grow]", "[][][]"));
        try {
            String filePath = "res/markers/markers25px/4x4Marker_" + robot.getId() + ".jpg";
            JLabel imageLabel = new JLabel(new ImageIcon(ImageIO.read(new File(filePath)).
                    getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            infoPanelContainer.add(imageLabel, "grow");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JPanel infoPanel = new JPanel(new MigLayout("fill", "[][fill, grow]", "[][][][][]"));
        infoPanel.add(new JLabel("State 1: "));
        infoPanel.add(state1Label, "wrap");
        infoPanel.add(new JLabel("State 2: "));
        infoPanel.add(state2Label, "wrap");
        infoPanel.add(new JLabel("State 3: "));
        infoPanel.add(state3Label, "wrap");
        infoPanel.add(new JLabel("Charge:  "));
        infoPanel.add(chargeLabel, "wrap");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(actionListener);
        infoPanel.add(refreshButton, "wrap, align left, span 2");
        infoPanelContainer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(115, 115, 115)), "Robot"));
        infoPanelContainer.add(infoPanel, "wrap, grow x");
        this.add(infoPanelContainer, "wrap, grow x");

        RobotControlPanel controlPanel = new RobotControlPanel(new Robot[]{robot}, communicationsRequester);
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(115, 115, 115)), "Control"));
        this.add(controlPanel, "wrap, span 2, grow x");

        updateInfo();
        timer.start();
        this.pack();
        this.setResizable(false);
        this.setVisible(true);

    }


    void updateInfo(){
        state1Label.setText(""+robot.getState1());
        state2Label.setText(""+robot.getState2());
        state3Label.setText(""+robot.getState3());
        chargeLabel.setText(""+(int)(robot.getChargeLevel()*100));
    }


    void setupEscapeClose() {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

}
