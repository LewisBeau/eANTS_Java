package com.eants.gui;

/**
 * Created by lewis on 02/09/2017.
 */
public interface UpdateListener {

    void connectedStateChange(int state);

    void queueLength(int length);

    void isProgramming(boolean isProgramming);

    void programmingProgress(double progress);

}
