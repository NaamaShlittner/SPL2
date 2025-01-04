package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.gson_files.StampedCloudPointsDeserializer;
import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static LiDarDataBase instance;
    private List<StampedCloudPoints> cloudPoints;

    private LiDarDataBase(String filePath) {
        this.cloudPoints = StampedCloudPointsDeserializer.getLidarData(filePath);
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }
        return instance;
    }

    public static LiDarDataBase getInstance() {
        return instance;
    }

    /**
     * Processes the DetectObjectsEvent and returns a list of TrackedObject instances.
     *
     * @param detectObjectsEvent The event containing detected objects data.
     * @return A list of TrackedObject instances.
     */
    public List<TrackedObject> getTrackedObjects(DetectObjectsEvent detectObjectsEvent) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        // for each detected object, find the corresponding cloud points and create a TrackedObject instance
        for (DetectedObject detectedObject : detectObjectsEvent.getDetectedObjects()) {
            ArrayList<CloudPoint> coordinates = new ArrayList<>();
            // find the cloud points corresponding to the detected object
            for (StampedCloudPoints stampedCloudPoints : cloudPoints) {
                if (stampedCloudPoints.getId().equals(detectedObject.getId())) {
                    coordinates.addAll(stampedCloudPoints.getCloudPoints());
                }
            }
            TrackedObject trackedObject = new TrackedObject(detectedObject.getId(), detectObjectsEvent.getTickTime(), detectedObject.getDescription(), coordinates);
            trackedObjects.add(trackedObject);
        }
        return trackedObjects;
    }

    public String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints.toString() +
                '}';
    }
}
