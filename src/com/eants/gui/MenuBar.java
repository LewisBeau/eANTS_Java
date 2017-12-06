package com.eants.gui;

import com.eants.communication.CommunicationsRequester;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by lewis on 26/08/2017.
 */
public class MenuBar extends JMenuBar {



    public MenuBar(CommunicationsRequester communicationsRequester) {


        /*
        *This mouselistener listens for clicks on the various menu items. It then registers
         */
        MouseListener menuListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                String name = "";
                if(e.getSource().getClass().equals(JMenuItem.class)){
                    name = ((JMenuItem) e.getSource()).getText();
                }else if (e.getSource().getClass().equals(JMenu.class)){
                    name = ((JMenu) e.getSource()).getText();
                }

                if (name.substring(1,2).equals(":")){
                    System.out.println(name.substring(0,1));
                    communicationsRequester.selectConnection(Integer.parseInt(name.substring(0,1)));
                    System.out.println(communicationsRequester.isOpen());
                }
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


        JMenu fileMenu = new JMenu("File");
        fileMenu.addMouseListener(menuListener);
        JMenu connectionMenu = new JMenu("Connection");
        connectionMenu.addMouseListener(menuListener);
        JMenu viewMenu = new JMenu("View");
        viewMenu.addMouseListener(menuListener);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.addMouseListener(menuListener);


        /*
         * File JMenu
         */
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addMouseListener(menuListener);
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addMouseListener(menuListener);
        JMenuItem newItem = new JMenuItem("New");
        newItem.addMouseListener(menuListener);
        JMenuItem aboutItem = new JMenuItem("About");

        JMenu connectItem = new JMenu("Connect");
        connectItem.addMouseListener(menuListener);


        /*
         * Enumerate and add initial ports
         */
        String[] ports = communicationsRequester.getPorts();
        connectionMenu.removeAll();
        for (int i = 0;i < ports.length;i++) {
            JMenuItem menuItem = new JMenuItem(i + ": " + ports[i]);
            menuItem.addMouseListener(menuListener);
            connectItem.add(menuItem);
        }


        /*
         * Put everything together
         */
        fileMenu.add(saveItem);
        fileMenu.add(connectItem);
        fileMenu.add(viewMenu);
        fileMenu.add(helpMenu);

        connectionMenu.add(connectItem);


        this.add(fileMenu);
        this.add(connectionMenu);
        this.add(viewMenu);
        this.add(helpMenu);


    }

}
