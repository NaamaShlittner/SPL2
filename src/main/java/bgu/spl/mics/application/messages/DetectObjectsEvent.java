package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DetectObjectsEvent implements Event<DetectedObject> {
    private final String cameraId;
    private final List<DetectedObject> detectedObjects;

    public DetectObjectsEvent(String cameraId, List<DetectedObject> detectedObjects) {
        this.cameraId = cameraId;
        this.detectedObjects = Collections.unmodifiableList(detectedObjects);
    }

    public String getCameraId() {
        return cameraId;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

}
