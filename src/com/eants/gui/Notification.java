package com.eants.gui;

import javax.naming.NoInitialContextException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by lewis on 02/09/2017.
 */
public class Notification extends JPanel{


    private Color color;
    public static final Color DeFAULT_COLOR = new Color(199, 187, 118);

    public Notification(String message){

        if(message.equals("Connected")){
            color = new Color(185, 246, 202);
        }else if(message.equals("Connecting")){
            color = new Color(255, 235, 59);
        }else if(message.equals("Disconnected")){
            color = new Color(255, 87, 34);
        }else if(message.equals("Idle")){
            color = new Color(213, 213, 213);
        }else {
            color= DeFAULT_COLOR;
        }


        this.setBackground(color);
        this.setOpaque(true);

        JLabel label = new JLabel(message);
        this.add(label);


    }





}
