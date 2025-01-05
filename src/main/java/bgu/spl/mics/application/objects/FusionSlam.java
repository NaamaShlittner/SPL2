package bgu.spl.mics.application.objects;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM). Combines data from multiple sensors (e.g., LiDAR, camera) to build
 * and update a global map. Implements the Singleton pattern to ensure a single
 * instance of FusionSlam exists.
 */
public class FusionSlam {
    private final Vector<LandMark> landmarks;
    private final Vector<Pose> poses;
    private final ConcurrentLinkedQueue<TrackedObject> objectsToProcess;
    private final AtomicInteger numOfActiveSensor = new AtomicInteger(0);

    private FusionSlam() {
        landmarks = new Vector<>();
        poses = new Vector<>();
        objectsToProcess = new ConcurrentLinkedQueue<>();
    }

    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    public void IncrementNumOfActiveSensor(){
        numOfActiveSensor.incrementAndGet();
    }

    public void DecrementNumOfActiveSensor(){
        numOfActiveSensor.decrementAndGet();
    }

    public int getNumOfActiveSensor(){
        return numOfActiveSensor.get();
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
        if (pose == null) {
            throw new IllegalArgumentException("Pose cannot be null");
        }
        poses.add(pose);
        processObjectPoses();
    }

    private final List<TrackedObject> waitingObjects = new ArrayList<>();

public synchronized void processObjectPoses() {
    Iterator<TrackedObject> trackedObjectIterator = objectsToProcess.iterator();
    while (trackedObjectIterator.hasNext()) {
        TrackedObject objectToProcess = trackedObjectIterator.next();
        Pose pose = poses.stream()
                .filter(p -> p.getTime() == objectToProcess.getTime())
                .findFirst()
                .orElse(null);

        if (pose == null) {
            System.out.println("Pose for time " + objectToProcess.getTime() + " not found. Moving to waiting list.");
            waitingObjects.add(objectToProcess); // Add to waiting list
            trackedObjectIterator.remove(); // Remove from objectsToProcess
            continue;
        }

        // Process object with the matching pose
        List<CloudPoint> globalCoordinates = transformToGlobalCoordinates(objectToProcess.getCoordinates(), pose);
        Optional<LandMark> existingLandmark = landmarks.stream()
                .filter(l -> l.getId().equals(objectToProcess.getId()))
                .findFirst();

        if (existingLandmark.isPresent()) {
            LandMark landmark = existingLandmark.get();
            List<CloudPoint> existingCoordinates = landmark.getCoordinates();
            List<CloudPoint> updatedCoordinates = new ArrayList<>();

            int k = Math.min(existingCoordinates.size(), globalCoordinates.size());
            for (int i = 0; i < k; i++) {
                CloudPoint li = existingCoordinates.get(i);
                CloudPoint ci = globalCoordinates.get(i);
                updatedCoordinates.add(new CloudPoint(
                        (li.getX() + ci.getX()) / 2,
                        (li.getY() + ci.getY()) / 2
                ));
            }

            if (globalCoordinates.size() > k) {
                updatedCoordinates.addAll(globalCoordinates.subList(k, globalCoordinates.size()));
            }

            landmarks.set(landmarks.indexOf(landmark),
                    new LandMark(objectToProcess.getId(), objectToProcess.getDescription(), updatedCoordinates));
        } else {
            landmarks.add(new LandMark(objectToProcess.getId(), objectToProcess.getDescription(), globalCoordinates));
            StatisticalFolder.getInstance().incrementNumLandmarks();
        }

        poses.removeIf(p -> p.getTime() == pose.getTime());
        trackedObjectIterator.remove(); // Successfully processed
    }

    // Reprocess waiting objects if new poses are added
    reprocessWaitingObjects();
}

private void reprocessWaitingObjects() {
    Iterator<TrackedObject> iterator = waitingObjects.iterator();
    while (iterator.hasNext()) {
        TrackedObject object = iterator.next();
        Pose pose = poses.stream()
                .filter(p -> p.getTime() == object.getTime())
                .findFirst()
                .orElse(null);

        if (pose != null) {
            System.out.println("Reprocessing object with time " + object.getTime());
            objectsToProcess.add(object);
            iterator.remove();
        }
    }
}
    public synchronized List<CloudPoint> transformToGlobalCoordinates(List<CloudPoint> relativeCoordinates, Pose pose) {
        double yawRadians = pose.getYaw() * Math.PI / 180;
        System.out.println("Yaw: " + yawRadians);
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);
        List<CloudPoint> globalCloudPoints = new ArrayList<>();
int i =0;
        for (CloudPoint relativeCloudPoint : relativeCoordinates) {
            System.out.println(i+"Relative cloud point: " + relativeCloudPoint);
            System.out.println(i+"Pose: " + pose.toString());
            double xGlobal = (cosTheta * relativeCloudPoint.getX() - sinTheta * relativeCloudPoint.getY() + pose.getX());
            System.out.println(i+"XGlobal: " + xGlobal);
            double yGlobal =  (sinTheta * relativeCloudPoint.getX() + cosTheta * relativeCloudPoint.getY() + pose.getY());
            System.out.println(i+"YGlobal: " + yGlobal);
            globalCloudPoints.add(new CloudPoint((float)xGlobal, (float)yGlobal));
            System.out.println(i+"Global cloud point: " + globalCloudPoints.get(globalCloudPoints.size() - 1));
            i++;
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

    public boolean isInLandmarks(String id){
        for(LandMark landmark: landmarks){
            if(landmark.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
}
