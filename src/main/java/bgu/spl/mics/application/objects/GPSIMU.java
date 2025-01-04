package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.application.gson_files.PoseDeserializer;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick = 0;
    private final List<Pose> poseList;
    private STATUS status = STATUS.UP;


    public GPSIMU(String gpsImuDataPath) {
        poseList = new ArrayList<Pose>();
        List<Pose> poses = PoseDeserializer.getPoseList(gpsImuDataPath);
        poseList.addAll(poses);

    }
    public synchronized  Pose getPoseAtTime(int time) {
        for (Pose pose : poseList) {
            if (pose.getTime() == time) {
                System.out.println("getPoseAtTime");
                return pose;
            }
        }
        return null;
    }

    public void add(Pose pose) {
        poseList.add(pose);
        notifyAll();
    }

    public String toString() {
        return "GPSIMU{" +
                "currentTick=" + currentTick +
                ", poseList=" + poseList.toString() +
                ", status=" + status +
                '}';
    }
}
