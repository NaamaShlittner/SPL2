package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick = 0;
    private final List<Pose> poseList;
    private STATUS status = STATUS.UP;


    public GPSIMU() {
        poseList = new ArrayList<Pose>();

    }
    public Pose getPoseAtTime(int time) {
        while (poseList.size() - 1 < time) {
            try{
                this.wait();
            } catch (InterruptedException e) {
                status = STATUS.ERROR;
                Thread.currentThread().interrupt();
            }
        }
        return poseList.get(time);
    }

    public void add(Pose pose) {
        poseList.add(pose);
        notifyAll();
    }
}
