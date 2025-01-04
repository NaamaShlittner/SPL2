package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService-" + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            if (camera.shouldSendData(tick.getTick())) {
                // here we know for a fact that the camera should send data and the list is not empty
                List<DetectedObject> detectedObjects = camera.detectObjects(tick.getTick() - camera.getFrequency());
                sendEvent(new DetectObjectsEvent(camera.getId(), detectedObjects,tick.getTick()));
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, Crash -> {
            System.out.println(("Sad Times :(")); // sus line O_o wtf should we do here
        });
    }
}
