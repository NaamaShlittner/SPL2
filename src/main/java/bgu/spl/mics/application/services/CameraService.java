package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.FusionSlam;

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
    final String GREEN = "\033[32m";
    final String PURPLE = "\033[35m";
    final String RESET = "\033[0m";

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
                for (DetectedObject detectedObject : detectedObjects) {
                    System.err.println(GREEN + "Camera " + camera.getId() + " detected " + detectedObject.getId() + RESET);
                    if (detectedObject.getId().equals("ERROR")) {
                        camera.crash();
                        sendBroadcast(new CrashedBroadcast(detectedObject.getDescription(), this.getName()));
                        terminate();
                        return;
                    }
                }
                System.out.println(PURPLE + "Camera " + camera.getId() + " detected " + detectedObjects.size() + " objects at tick " + tick.getTick() + RESET);
                sendEvent(new DetectObjectsEvent(camera.getId(), detectedObjects,tick.getTick()));
                FusionSlam.getInstance().updateDataPassedBySensors("Camera-" + camera.getId(), camera.getStampedDetectedObjects(tick.getTick() - camera.getFrequency())); // sending last frame data to FusionSlam
            }
            if (camera.isFinished(tick.getTick())) {
                camera.terminate();
                terminate();
            }
        });
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            if (broadcast.getSenderClass().equals(TimeService.class)) {
                camera.terminate();
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, Crash -> {
            camera.terminate();
            terminate();
        });
        FusionSlam.getInstance().IncrementNumOfActiveSensor();
    }
}
