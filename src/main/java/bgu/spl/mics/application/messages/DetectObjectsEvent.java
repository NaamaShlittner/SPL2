package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DetectObjectsEvent implements Event<DetectedObject> {
    private final String cameraId;
    private final List<DetectedObject> detectedObjects;
    private final int tickTime;

    public DetectObjectsEvent(String cameraId, List<DetectedObject> detectedObjects, int tickTime) {
        this.cameraId = cameraId;
        this.detectedObjects = Collections.unmodifiableList(detectedObjects);
        this.tickTime = tickTime;
    }

    public String getCameraId() {
        return cameraId;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public int getTickTime() {
        return tickTime;
    }

    public String toString() {
        return "DetectObjectsEvent: " + detectedObjects.stream().map(DetectedObject::toString).collect(Collectors.joining(", "))
                + ", Camera=" + cameraId + ", time=" + tickTime;
    }

}
