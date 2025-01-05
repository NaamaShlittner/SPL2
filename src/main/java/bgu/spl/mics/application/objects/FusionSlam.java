package bgu.spl.mics.application.objects;

import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM). Combines data from multiple sensors (e.g., LiDAR, camera) to build
 * and update a global map. Implements the Singleton pattern to ensure a single
 * instance of FusionSlam exists.
 */
public class FusionSlam {
    private final List<LandMark> landmarks;
    private final List<Pose> poses;
    private final Queue<TrackedObject> objectsToProcess;

    private FusionSlam() {
        landmarks = new ArrayList<>();
        poses = new ArrayList<>();
        objectsToProcess = new LinkedList<>();
    }

    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;
    }

    // Updates the global map with tracked objects
    public synchronized void updateTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject trackedObject : trackedObjects) {
            objectsToProcess.add(trackedObject);
        }
        processObjectPoses();
    }

    // Updates the robot's pose
    public synchronized void updatePose(Pose pose) {
        poses.add(pose);
        processObjectPoses();
    }

    public synchronized void processObjectPoses() {
        List<CloudPoint> currentObjectCoordinates;
        CloudPoint currentObjectCoordinatesAverage;
        int lastAvailablePose = poses.get(poses.size() - 1).getTime();
        boolean isObjectInLandmarks = false;


        // for each object to process{
            // for each pose{
                // if object time == pose time{
                // do calculations
                // remove pose from poses
                //}
            // remove object from objects to process
            //}
        //}
        while (!objectsToProcess.isEmpty() && objectsToProcess.peek().getTime() <= lastAvailablePose) {
            TrackedObject objectToProcess = objectsToProcess.remove();
            currentObjectCoordinates = transformToGlobalCoordinates(objectToProcess.getCoordinates(),
                    poses.get(objectToProcess.getTime() - 1));
            currentObjectCoordinatesAverage = calculateCoordinatesAverage(currentObjectCoordinates);

            for (int i = 0; i < landmarks.size(); i++) {
                if (landmarks.get(i).getId().equals(objectToProcess.getId())) {
                    CloudPoint newCoords = new CloudPoint(
                            (landmarks.get(i).getCoordinates().get(0).getX() + currentObjectCoordinatesAverage.getX()) / 2,
                            (landmarks.get(i).getCoordinates().get(0).getY() + currentObjectCoordinatesAverage.getY()) / 2);
                    landmarks.set(i,
                            new LandMark(objectToProcess.getId(), objectToProcess.getDescription(), List.of(newCoords)));
                    isObjectInLandmarks = true;
                }
            }
            if (!isObjectInLandmarks) {
                landmarks.add(new LandMark(objectToProcess.getId(), objectToProcess.getDescription(),
                        List.of(currentObjectCoordinatesAverage)));
                StatisticalFolder.getInstance().incrementNumLandmarks();
            }
            isObjectInLandmarks = false;
        }
    }

    public List<CloudPoint> transformToGlobalCoordinates(List<CloudPoint> relativeCoordinates, Pose pose) {
        double yawRadians;
        double xGlobal;
        double yGlobal;
        List<CloudPoint> globalCloudPoints = new ArrayList<>();

        for (CloudPoint relativeCloudPoint : relativeCoordinates) {
            yawRadians = pose.getYaw() * Math.PI / 180;
            xGlobal = (Math.cos(yawRadians) * relativeCloudPoint.getX())
                    - (Math.sin(yawRadians) * relativeCloudPoint.getY()) + pose.getX();
            yGlobal = (Math.sin(yawRadians) * relativeCloudPoint.getX())
                    + (Math.cos(yawRadians) * relativeCloudPoint.getY()) + pose.getY();
            globalCloudPoints.add(new CloudPoint((float)xGlobal, (float)yGlobal));
        }
        return globalCloudPoints;
    }

    public CloudPoint calculateCoordinatesAverage(List<CloudPoint> cloudPoints) {
        int coordinatesCount = cloudPoints.size();
        float x = 0;
        float y = 0;
        for (CloudPoint coordinates : cloudPoints) {
            x += coordinates.getX();
            y += coordinates.getY();
        }
        return new CloudPoint(x / coordinatesCount, y / coordinatesCount);
    }

    // Performs periodic updates
    public void performPeriodicUpdate(int tick) {
        // Implement logic for periodic updates
        // For example:
        // - Clean up old data
        // - Recalculate map based on new data
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public List<Pose> getPoses() {
        return poses;
    }

    /**
     * Handles a crash by performing necessary cleanup or state updates.
     * @param crashDetails The details of the crash.
     */
    public synchronized void handleCrash(String crashDetails) {
        System.out.println("Handling crash: " + crashDetails);
        // Perform necessary cleanup or state updates
        landmarks.clear();
        poses.clear();
        objectsToProcess.clear();
    }
}
