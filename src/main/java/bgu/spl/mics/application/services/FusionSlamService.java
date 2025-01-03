package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.messages.*;
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
        subscribeBroadcast(TickBroadcast.class, tick -> {
            statisticalFolder.setSystemRuntime(tick.getTick());
        });

        // Subscribe to TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            fusionSlam.processTrackedObjects(event.getTrackedObjects());
            statisticalFolder.incrementNumTrackedObjects();
            complete(event, event.getTrackedObjects());
        });

        // Subscribe to PoseEvent
        subscribeEvent(PoseEvent.class, event -> {
            fusionSlam.updatePose(event.getPose());
            complete(event, event.getPose());
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            writeOutputFile();
            terminate();
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            System.out.println(("Sad Times :(")); // sus line O_o wtf should we do here
        });
    }

    private void writeOutputFile() {
        Map<String, Object> output = new HashMap<>();
        output.put("systemRuntime", statisticalFolder.getSystemRuntime());
        output.put("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
        output.put("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
        output.put("numLandmarks", statisticalFolder.getNumLandmarks());
        output.put("landMarks", fusionSlam.getLandmarks());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeErrorOutputFile(String error, String faultySensor, Object lastFrames, Object poses) {
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
