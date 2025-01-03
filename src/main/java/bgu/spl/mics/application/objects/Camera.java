package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.application.gson_files.StampedDetectedObjectsDeserializer;
/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final String id;
    private final int frequency;
    private STATUS status = STATUS.UP;
    private final List<StampedDetectedObjects> detectedObjectsList;

    public Camera(String id, int frequency, String key, String cameraDataPath) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsList = new ArrayList<>();
        List<StampedDetectedObjects> stampedDetectedObjects = StampedDetectedObjectsDeserializer.getCameraData(cameraDataPath, key);
        this.detectedObjectsList.addAll(stampedDetectedObjects);
    }

    public String getId() {
        return id;
    }

    public boolean shouldSendData(int tick){
        return tick % frequency == 0;
    }

    public List<DetectedObject> detectObjects(int tick) {
        synchronized (detectedObjectsList) {
            List<DetectedObject> result = new ArrayList<>();
            for (StampedDetectedObjects stampedObject : detectedObjectsList) {
                if (stampedObject.getTime() == tick) {
                    result.addAll(stampedObject.getDetectedObjects());
                }
            }
            return result;
        }
    }
    public String toString() {
        return "Camera{" +
                "id='" + id + '\'' +
                ", frequency=" + frequency +
                ", status=" + status +
                ", detectedObjectsList=" + detectedObjectsList.toString() +
                '}';
    }
}
