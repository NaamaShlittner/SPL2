package bgu.spl.mics.application.objects;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        poses.add(pose);
        processObjectPoses();
    }

    public synchronized void processObjectPoses() {
        List<CloudPoint> currentObjectCoordinates;
        CloudPoint currentObjectCoordinatesAverage;

        Iterator<TrackedObject> trackedObjectIterator = objectsToProcess.iterator();
        while (trackedObjectIterator.hasNext()) {
            TrackedObject objectToProcess = trackedObjectIterator.next();
            Iterator<Pose> poseIterator = poses.iterator();
            while(poseIterator.hasNext()){
                Pose pose = poseIterator.next();
                if (objectToProcess.getTime() == pose.getTime()) {
                    currentObjectCoordinates = transformToGlobalCoordinates(objectToProcess.getCoordinates(),pose);
                    currentObjectCoordinatesAverage = calculateCoordinatesAverage(currentObjectCoordinates);
                    if (!isInLandmarks(objectToProcess.getId())) {
                        landmarks.add(new LandMark(objectToProcess.getId(), objectToProcess.getDescription(), List.of(currentObjectCoordinatesAverage)));
                        StatisticalFolder.getInstance().incrementNumLandmarks();
                    }
                    for(LandMark landmark: landmarks){
                        if(landmark.getId().equals(objectToProcess.getId())){
                            CloudPoint newCoords = new CloudPoint(
                                    (landmark.getCoordinates().get(0).getX() + currentObjectCoordinatesAverage.getX()) / 2,
                                    (landmark.getCoordinates().get(0).getY() + currentObjectCoordinatesAverage.getY()) / 2);
                            landmarks.set(landmarks.indexOf(landmark),
                                    new LandMark(objectToProcess.getId(), objectToProcess.getDescription(), List.of(newCoords)));
                        }
                    }
                    poses.remove(pose);
                    objectsToProcess.remove(objectToProcess);
                    break;
                }
            }     
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

    public boolean isInLandmarks(String id){
        for(LandMark landmark: landmarks){
            if(landmark.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public boolean isTimeToTerminate() {
        System.out.println("num of active sensors: " + numOfActiveSensor.get() + " objects to process: " + objectsToProcess.size());
        return numOfActiveSensor.get() == 0;
    }
}
