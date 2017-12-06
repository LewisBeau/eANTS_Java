package com.eants.gui;

import com.eants.communication.AutoUpdater;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by lewis on 18/09/2017.
 */
public class AutoUpdateFrame extends JFrame {

    private AutoUpdater autoUpdater;

    private JCheckBox sense1Box;
    private JCheckBox sense2Box;
    private JCheckBox sense3Box;

    private JTextField frequencyField;
    private JCheckBox speedBox;
    private JCheckBox chargeBox;
    private JCheckBox orientationBox;
    private JCheckBox status1Box;
    private JCheckBox status2Box;
    private JCheckBox status3Box;

    private JRadioButton enableButton;
    private JRadioButton disableButton;


    public AutoUpdateFrame(AutoUpdater autoUpdater){
        this.autoUpdater = autoUpdater;

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setSize(dimension.width / 5, (dimension.width / 10) * 3);
        this.setLocation(dimension.width / 2, dimension.height / 3);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon imageIcon = new ImageIcon("res/eantsicon.png");
        this.setIconImage(imageIcon.getImage());



        JPanel contentPane = new JPanel(new MigLayout("fill","8mm[][fill, grow]8mm", "8mm[][][][][][][][][][][]8mm"));
        TitledBorder border = new TitledBorder(BorderFactory.createLineBorder(new Color(127,127,126)), "Auto Update");
        contentPane.setBorder(border);

        enableButton = new JRadioButton("Enable");
        disableButton = new JRadioButton("Disable");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(enableButton);
        buttonGroup.add(disableButton);
        enableButton.setSelected(true);

        contentPane.add(enableButton);
        contentPane.add(disableButton, "wrap");

        sense1Box = new JCheckBox("Sense 1");
        contentPane.add(sense1Box, "wrap");
        sense2Box = new JCheckBox("Sense 2");
        contentPane.add(sense2Box, "wrap");
        sense3Box = new JCheckBox("Sense 3");
        contentPane.add(sense3Box, "wrap");

        status1Box = new JCheckBox("Status 1");
        contentPane.add(status1Box, "wrap");
        status2Box = new JCheckBox("Status 2");
        contentPane.add(status2Box, "wrap");
        status3Box = new JCheckBox("Status 3");
        contentPane.add(status3Box, "wrap");
        chargeBox = new JCheckBox("Charge");
        contentPane.add(chargeBox, "wrap");
        speedBox = new JCheckBox("Speed");
        contentPane.add(speedBox, "wrap");
        orientationBox = new JCheckBox("Orientation");
        contentPane.add(orientationBox, "wrap");

        frequencyField = new JTextField("5");
        contentPane.add(new JLabel("Frequency (s)"));
        contentPane.add(frequencyField, "wrap, grow x");

        JButton setButton = new JButton("Set");
        setButton.addActionListener(e -> {
            autoUpdater.setCharge(chargeBox.isSelected());
            autoUpdater.setSpeed(speedBox.isSelected());
            autoUpdater.setOrientation(orientationBox.isSelected());
            autoUpdater.setStatus1(status1Box.isSelected());
            autoUpdater.setStatus2(status2Box.isSelected());
            autoUpdater.setStatus3(status3Box.isSelected());
            autoUpdater.setFrequency(Integer.parseInt(frequencyField.getText()));
            autoUpdater.setEnabled(enableButton.isSelected());
            autoUpdater.setSense1(sense1Box.isSelected());
            autoUpdater.setSense2(sense2Box.isSelected());
            autoUpdater.setSense3(sense3Box.isSelected());
        });

        contentPane.add(setButton);

        this.add(contentPane, BorderLayout.CENTER);
        setupEscapeClose();
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
