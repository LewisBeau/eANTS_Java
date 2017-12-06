package com.eants.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.KeyEvent;


/**
 * Created by lewis on 26/08/2017.
 */
public class CommandPanel extends JPanel{

    private JTextArea textArea;

    private JScrollPane scrollPane;
    private JButton sendButton;

    private JTextField commandLine;


    private JPanel topBar;
    private JPanel bottomBar;

    private JButton clearButton;

    public CommandPanel(){

        this.setLayout(new MigLayout("fill", "[]","[shrink][grow][shrink]" ));


        /*
        Code for the control top-bar
         */
        topBar = new JPanel();

        JLabel topBarLabel1 = new JLabel("Robot");

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearPressed());

        topBar.add(topBarLabel1);
        topBar.add(clearButton);

        /*
        Code for the central text area
         */
        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);

        /*
        Code for the bottom bar
         */
        bottomBar = new JPanel(new MigLayout("", "[grow][shrink]", "[]"));

        //Items
        commandLine = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendPressed());
        sendButton.setMnemonic(KeyEvent.VK_ENTER);

        bottomBar.add(commandLine,"growx");
        bottomBar.add(sendButton);

        //Put everything together

        this.add(topBar, "wrap");
        this.add(scrollPane, "wrap, growx, growy");
        this.add(bottomBar, "growx");



    }

    public void log(String message){
        textArea.append(message);
        repaint();
    }

    void sendPressed(){
        textArea.append("\n>" + commandLine.getText());
    }

    void clearPressed(){
        textArea.setText("");
        commandLine.setText("");
    }

}
