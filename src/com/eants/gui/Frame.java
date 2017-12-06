package com.eants.gui;



import com.eants.communication.CommunicationsRequester;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by lewis on 26/08/2017.
 *
 * This is the main frame, which contains all of the GUI
 */
public class Frame extends JFrame {


    public static final Color BACKGROUND_COLOR_DARK = new Color(47, 47, 47);
    public static final Color BACKGROUND_COLOR_LIGHT = new Color(115, 115, 115);
    public static final Color ELEMENT_DARK_COLOR = new Color(47,47,47);
    public static final Color ELEMENT_LIGHT_COLOR = new Color(115,115,115);
    public static final Color TEXT_COLOR = new Color(230, 230, 230);

    /**
     * The main content pane
     */
    private JTabbedPane tabbedPane;

    /**
     * This is the default notification to be displayed at the bottom of the screen
     */
    private JPanel notification = new Notification("Idle");

    /**
     * This listens for status updates on the arduino connection
     */
    private UpdateListener updateListener;


    /**
     * This sets up the main frame
     * @param comRequester the interface for general communication
     */
    public Frame(CommunicationsRequester comRequester) {




        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(d.width / 5, d.height / 6, d.width / 2, (d.height / 3) * 2);
        this.setTitle("eANTS 1.0 Beta");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        ImageIcon imageIcon = new ImageIcon("res/eantsicon.png");
        this.setIconImage(imageIcon.getImage());

        this.setLayout(new MigLayout("fill", "[grow]", "[grow][shrink]"));


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setUIFont(new FontUIResource(new Font("PT Sans", Font.PLAIN, 28)));
        //setUIFont(new FontUIResource(new Font("Ikaro Sans", Font.PLAIN, 25)));


        //setUIFont(new FontUIResource(new Font("Oswald", Font.PLAIN, 25)));
        //this.getContentPane().setBackground(new Color(33, 150, 243));

        MenuBar menuBar = new MenuBar(comRequester);

        this.setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();

        this.add(tabbedPane, ("growx, growy, wrap"));


        updateListener = new UpdateListener() {
            @Override
            public void connectedStateChange(int state) {
                if (state == 0){
                    addNotification(new Notification("Disconnected"));
                }else if(state == 1){
                    addNotification(new Notification("Connected"));
                }
            }

            @Override
            public void queueLength(int length) {
                addNotification(new Notification("Connected"));
            }

            @Override
            public void isProgramming(boolean isProgramming) {

            }

            @Override
            public void programmingProgress(double progress) {

            }
        };


    }

    /**
     * Gets the updatelistener interface, on which updates to the notification and other UI
     * elements are registered by the Arduino
     * @return
     */
    public UpdateListener getUpdateListener(){
        return updateListener;
    }

    /**
     * Adds a tab with icon to the frame
     * @param name the name of the tab
     * @param tabPanel the JPanel to be added
     * @param icon the icon of the current tab
     */
    public void addTab(String name, JPanel tabPanel, ImageIcon icon) {
        tabbedPane.addTab(name, icon, tabPanel);
    }

    /**
     * Adds a new tab to the frame
     * @param name The name of the tab
     * @param tabPanel The JPanel to be added to the tabbed pane
     */
    public void addTab(String name, JPanel tabPanel) {
        addTab(name, tabPanel, null);
    }


    /**
     * Sets a nice font
     * @param f the font resource
     */
    public static void setUIFont(FontUIResource f) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

    /**
     * sets the current notification at the bottom of the fram
     * @param notification The notification to be displayed
     */
    public void addNotification(Notification notification) {
        if (notification != null)
            this.remove(this.notification);
        this.notification = notification;
        this.add(this.notification, BorderLayout.SOUTH);
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void log(String message){

    }



}
