package com.eants.gui;

import com.eants.communication.CommunicationsRequester;
import com.eants.robots.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by lewis on 18/09/2017.
 */
public class RobotControlFrame extends JFrame{

    public RobotControlFrame(Robot[] robots, CommunicationsRequester communicationsRequester){
        setupEscapeClose();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setSize(dimension.width / 5, (dimension.width / 10) * 3);
        this.setLocation(dimension.width / 2, dimension.height / 3);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon imageIcon = new ImageIcon("res/eantsicon.png");
        this.setIconImage(imageIcon.getImage());

        //this.setLayout(new MigLayout("fill", "[fill, grow]", "[]"));
        this.add(new RobotControlPanel(robots, communicationsRequester), BorderLayout.CENTER);

        this.setResizable(false);
        this.pack();
        this.setVisible(true);

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
