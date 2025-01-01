package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    private StatisticalFolder statisticalFolder;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            statisticalFolder.setSystemRuntime(tick.getTick());
        });
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            fusionSlam.processTrackedObjects(event.getTrackedObjects());
            event.getTrackedObjects().forEach(trackedObject -> {
                statisticalFolder.incrementNumLandmarks();
            });
            complete(event, true);
        });
        subscribeEvent(PoseEvent.class, event -> {
            fusionSlam.updatePose(event.getPose());
            complete(event, true);
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            fusionSlam.handleCrash(crashed.getCrashDetails());
        });
    }
}
