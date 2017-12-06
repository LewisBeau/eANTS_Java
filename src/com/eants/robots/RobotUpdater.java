package com.eants.robots;

/**
 * Created by lewis on 03/09/2017.
 */
public interface RobotUpdater {

    /**
     * This provides the information returned from the communicator, ready to be displayed in the GUI
     */
    public void updateState(RobotUpdate robotUpdate);

    public void updateProgramProgress(float progress);


}
