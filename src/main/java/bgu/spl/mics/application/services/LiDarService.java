package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarWorkerTracker;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService");
        this.liDarWorkerTracker = LiDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // sus method O_o
        // needs rewriting asap
        subscribeEvent(DetectObjectsEvent.class, detectObjectsEvent -> {
            List<TrackedObject> trackedObjects = liDarWorkerTracker.processData(detectObjectsEvent);
            sendEvent(new TrackedObjectsEvent(trackedObjects));
        });
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, Crash -> {
            System.out.println(("Sad Times :(")); // sus line O_o wtf should we do here
        });

        subscribeBroadcast(TickBroadcast.class, tick -> {});
    }
}
