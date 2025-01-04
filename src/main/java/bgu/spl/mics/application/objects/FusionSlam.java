package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final List<LandMark> landmarks;
    private final List<Pose> poses;

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    private FusionSlam() {
        this.landmarks = new ArrayList<>();
        this.poses = new ArrayList<>();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;
    }

    public synchronized List<LandMark> getLandmarks() {
        return new ArrayList<>(landmarks);
    }

    public synchronized void addLandmark(LandMark landmark) {
        landmarks.add(landmark);
    }

    public synchronized void addPose(Pose pose) {
        poses.add(pose);
    }

    public synchronized void processTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject obj : trackedObjects) {
            addLandmark(obj.getLandmark());
            addPose(transformCoordinates(obj.getPose()));
        }
    }

    public synchronized void updatePose(Pose pose) {
        addPose(transformCoordinates(pose));
    }

    public Pose transformCoordinates(Pose pose) {
        double thetaRad = Math.toRadians(pose.getYaw());
        double cosTheta = Math.cos(thetaRad);
        double sinTheta = Math.sin(thetaRad);

        double xGlobal = cosTheta * pose.getX() - sinTheta * pose.getY() + pose.getX();
        double yGlobal = sinTheta * pose.getX() + cosTheta * pose.getY() + pose.getY();

        return new Pose((float)xGlobal, (float)yGlobal, pose.getYaw(),pose.getTime());
    }

    public synchronized void handleCrash(String crashDetails) {
        // System.out.println("Handling crash: " + crashDetails);
        landmarks.clear();
        poses.clear();
    }
}
