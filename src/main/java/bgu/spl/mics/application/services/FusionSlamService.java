package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.LandMark;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;
    private final StatisticalFolder statisticalFolder;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     * @param statisticalFolder The StatisticalFolder object responsible for aggregating key metrics.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
        this.statisticalFolder = statisticalFolder;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            statisticalFolder.setSystemRuntime(tick.getTick());
            fusionSlam.performPeriodicUpdate(tick.getTick());
        });

        // Subscribe to TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent event) -> {
            fusionSlam.updateTrackedObjects(event.getTrackedObjects());
            statisticalFolder.incrementNumTrackedObjects();
            complete(event, event.getTrackedObjects());
        });

        // Subscribe to PoseEvent
        subscribeEvent(PoseEvent.class, (PoseEvent event) -> {
            fusionSlam.updatePose(event.getPose());
            complete(event, event.getPose());
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminated) -> {
            writeOutputFile();
            terminate();
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast crashed) -> {
            fusionSlam.handleCrash(crashed.getCrashDetails());
            writeErrorOutputFile(crashed.getCrashDetails(), crashed.getFaultySensor(), crashed.getLastFrames(), crashed.getPoses());
        });
    }

    private void writeOutputFile() {
        Map<String, Object> output = new HashMap<>();
        output.put("systemRuntime", statisticalFolder.getSystemRuntime());
        output.put("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        output.put("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        output.put("numLandmarks", statisticalFolder.getNumLandmarks());

        Map<String, Object> landmarksMap = new HashMap<>();
        for (LandMark landmark : fusionSlam.getLandmarks()) {
            Map<String, Object> landmarkDetails = new HashMap<>();
            landmarkDetails.put("id", landmark.getId());
            landmarkDetails.put("description", landmark.getDescription());
            landmarkDetails.put("coordinates", landmark.getCoordinates());
            landmarksMap.put(landmark.getId(), landmarkDetails);
        }
        output.put("landMarks", landmarksMap);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeErrorOutputFile(String error, String faultySensor, Object lastFrames, List<Pose> poses) {
        Map<String, Object> output = new HashMap<>();
        output.put("Error", error);
        output.put("faultySensor", faultySensor);
        output.put("lastFrames", lastFrames);
        output.put("poses", poses);
        output.put("statistics", getStatistics());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("systemRuntime", statisticalFolder.getSystemRuntime());
        statistics.put("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        statistics.put("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        statistics.put("numLandmarks", statisticalFolder.getNumLandmarks());
        return statistics;
    }
}
