package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static LiDarDataBase instance;
    private List<StampedCloudPoints> cloudPoints;

    private LiDarDataBase(String filePath) {
        cloudPoints = loadCloudPointsFromFile(filePath);
    }

    private List<StampedCloudPoints> loadCloudPointsFromFile(String filePath) {
        if (!Files.exists(Paths.get(filePath))) {
            throw new RuntimeException("File not found: " + filePath);
        }

        try {
            return new Gson().fromJson(
                    new FileReader(filePath),
                    new TypeToken<List<StampedCloudPoints>>() {}.getType()
            );
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
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

    public synchronized List<TrackedObject> getTrackedObjects(DetectObjectsEvent event) {
        List<DetectedObject> detectedObjects = event.getDetectedObjects();
        List<TrackedObject> trackedObjects = new ArrayList<>();

        for (DetectedObject detectedObject : detectedObjects) {
            String objectId = detectedObject.getId();
            int trackingTime = event.getTime(); // נניח שהאירוע מכיל את זמן הזיהוי
            String description = "Tracked object from camera " + event.getCameraId(); // תיאור מותאם אישית
            ArrayList<CloudPoint> coordinates = new ArrayList<>();

            // יצירת רשימת CloudPoints מתוך DetectedObject (נניח שהיא מכילה אותם)
            coordinates.add(new CloudPoint(detectedObject.getX(), detectedObject.getY()));

            // יצירת TrackedObject חדש והוספתו לרשימה
            TrackedObject trackedObject = new TrackedObject(objectId, trackingTime, description, coordinates);
            trackedObjects.add(trackedObject);
        }

        // החזרת הרשימה של האובייקטים שנעקבו
        return trackedObjects;
    }
}
