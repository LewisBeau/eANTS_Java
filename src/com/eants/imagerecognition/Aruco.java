package com.eants.imagerecognition;

import com.eants.robots.ArucoUpdate;
import com.eants.robots.RobotManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

/**
 * Created by lewis on 12/09/2017.
 */

public class Aruco {

    static {
        System.loadLibrary("ArucoInterface");
    }

    public native static boolean startVideo(int channel);

    public native static byte[] getImage();
    public native static byte[] getPaintedImage();

    public native static boolean isOpen();
    public native static int findRobots();

    public native static int getRobotCentrePointX(int address);
    public native static int getRobotCentrePointY(int address);
    public native static int getRobotRadius(int address);
    public native static int getRobotRotation(int address);

    public native static int getImageWidth();
    public native static int getImageHeigth();

    private ArucoPanel arucoPanel;

    private int width;
    private int height;

    private Timer refreshTimer;


    private RobotManager robotManager;


    public Aruco(int channel, RobotManager robotManager){

        this.robotManager = robotManager;
    }

    public JPanel getOutputPanel(){
        OpenListener openListener = new OpenListener() {
            @Override
            public boolean openChannel(int channel) {
                setEnabled(true, channel);
                if (isOpen())return true;
                return false;
            }
        };
        arucoPanel = new ArucoPanel(openListener, robotManager);
        return arucoPanel;
    }


    public void setEnabled(boolean value, int channel){
        arucoPanel.setOpenEnable(false);
        startVideo(channel);
        findRobots();

        this.width = getImageWidth();
        this.height = getImageHeigth();

        if(value){
            refreshTimer = new Timer(33, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    synchronized(this) {
                        if (isOpen()){
                            findRobots();
                            for (int i = 0;i <= 200;i++){
                                int centrePointX = getRobotCentrePointX(i);
                                if (centrePointX != -1){
                                    int centrePointY = getRobotCentrePointY(i);
                                    int orientation = getRobotRotation(i);
                                    int radius = getRobotRadius(i);
                                    robotManager.updateState(new ArucoUpdate(i, centrePointX,centrePointY,radius, orientation));
                                }
                            }
                            if (arucoPanel != null){
                                //getImage();
                                //getPaintedImage();
                                arucoPanel.setVideoImage(byte2buffered(getImage(), width, height));
                                arucoPanel.setOutputImage(byte2buffered(getPaintedImage(), width, height));
                                arucoPanel.repaint();
                            }
                        }else {
                            refreshTimer.stop();
                            arucoPanel.setOpenEnable(true);
                        }

                    }
                }
            });
            refreshTimer.start();
        }else {
            arucoPanel.setOpenEnable(false);
            if (refreshTimer != null){
                refreshTimer.stop();
            }
        }
    }


    /**
     *
     */
    int[] getAddresses(){
        ArrayList<Integer> addressesList = new ArrayList<>();
        for (int i = 0;i < 201;i++){
            if (getRobotCentrePointX(i) != -1){
                addressesList.add(i);
            }
        }
        int[] addresses = new int[addressesList.size()];
        for (int i = 0;i < addresses.length;i++) {
            addresses[i] = addressesList.get(i);
        }
        return  addresses;
    }


    /**
     * This takes a byte array and retuns a buffered image. The byte array is usually sourced from the
     * @param pixels the bytes with the pixel values
     * @param width the image width
     * @param height the image height
     * @return the buffered image
     */
    private static BufferedImage byte2buffered(byte[] pixels, int width, int height){
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        //System.out.println(width + " " + height);
        byte[] imgData = ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, imgData, 0, pixels.length);

        //For some reason the image occupies the upper left corner... The following code scales it so that the
        //web cam image occupies the whole buffered image
        /*BufferedImage transformedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(2.0, 3.0);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        transformedImage = scaleOp.filter(bufferedImage, transformedImage);*/

        return bufferedImage;
    }


}
