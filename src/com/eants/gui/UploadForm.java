package com.eants.gui;


import com.eants.communication.CommunicationsRequester;
import com.eants.communication.ProgramRequest;
import com.eants.robots.RobotManager;
import com.eants.robots.Robot;
import net.miginfocom.swing.MigLayout;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lewis on 03/09/2017.
 *
 * This form is responsible for uploading code to the robots via the arduino
 *
 */

public class UploadForm extends JFrame {

    private ActionListener actionListener;

    private JLabel stateLabel;
    private JProgressBar progressBar;

    private JButton programButton;

    private JLabel fileLabel;
    private File file;

    private RobotManager robotManager;
    private CommunicationsRequester communicationsRequester;

    private JFileChooser chooser;

    public UploadForm(RobotManager robotManager, CommunicationsRequester communicationsRequester) {

        this.robotManager = robotManager;

        this.communicationsRequester = communicationsRequester;

        actionListener = e -> {
            if (e.getActionCommand().equals("File")) {
                fileChoose();
            }else if (e.getActionCommand().equals("Program!")){
                program();
            }
        };


        Robot[] robots = robotManager.getSelectedRobots();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setSize(dimension.width / 5, (dimension.width / 10) * 3);
        this.setLocation(dimension.width / 2, dimension.height / 3);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new MigLayout("fill", "10mm[grow][grow]10mm", "10mm[][][]5mm"));

        ImageIcon imageIcon = new ImageIcon("res/eantsicon.png");
        this.setIconImage(imageIcon.getImage());

        setupEscapeClose();
        //this.setTitle("Programming " + robots.length + " robot(s)");
        this.setTitle("eANTS");

        JLabel titleLabel = new JLabel("Programming " + robots.length + " robot(s)");

        JLabel label1 = new JLabel("Choose File");
        JButton fileButton = new JButton("File");
        fileButton.addActionListener(actionListener);
        fileLabel = new JLabel("No File");
        fileLabel.setEnabled(false);

        programButton = new JButton("Program!");
        programButton.addActionListener(actionListener);

        stateLabel = new JLabel("Idle");
        progressBar = new JProgressBar(0, 100);


        BufferedImage robotImage;
        try {
            robotImage = ImageIO.read(new File("res/upload.png"));
        } catch (IOException e) {
            e.printStackTrace();
            robotImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        }
        Image scaledImage = robotImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        contentPane.add(titleLabel, "span2, wrap, grow");
        contentPane.add(imageLabel, "span2, growx, wrap");
        contentPane.add(label1);
        contentPane.add(fileButton, "wrap, shrink");
        contentPane.add(fileLabel, "wrap, span2");
        contentPane.add(programButton, "span2, wrap");
        contentPane.add(stateLabel, "wrap");
        contentPane.add(progressBar, "span2, growx");


        String state = communicationsRequester.isOpen() ? "Connected" : "Disconnected";
        contentPane.add(new Notification(state), BorderLayout.SOUTH);

        setProgramEnable(false);

        this.add(contentPane);
        //this.getContentPane().setPreferredSize(new Dimension(500,500));
        this.pack();
        this.setResizable(false);
        this.setVisible(true);

    }


    /**
     * Disables the programming feature so asa to avoid illegal programming
     * @param value
     */
    private void setProgramEnable(boolean value) {
        progressBar.setEnabled(value);
        stateLabel.setEnabled(value);
        programButton.setEnabled(value);
    }

    /**
     * Writes code from file to selected robots
     */
    private void program(){
        Robot[] robots = robotManager.getSelectedRobots();
        ProgramRequest programRequest = new ProgramRequest(robots, file);
        communicationsRequester.requestProgram(programRequest);

    }

    /**
     * Chooses a file for uploading
     */
    private void fileChoose(){
        //Select file for uploading
        chooser = new JFileChooser();
                    /* filter = new FileNameExtensionFilter(
                            "ino.hex", "hex");
                    chooser.setFileFilter(filter);*/
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileLabel.setText(chooser.getSelectedFile().getName());
            this.file = chooser.getSelectedFile();
            if (robotManager.getSelectedRobots().length > 0) {
                setProgramEnable(true);
            } else {
                setProgramEnable(false);
            }
        }
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
