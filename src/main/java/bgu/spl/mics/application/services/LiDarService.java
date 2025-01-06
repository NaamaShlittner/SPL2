package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.Iterator;

import java.util.ArrayList;
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
    private List<TrackedObjectsEvent> trackedObjectsEventsWaiting;
    final String PURPLE = "\033[35m";
    final String RESET = "\033[0m";

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService-" + LiDarWorkerTracker.getId());
        trackedObjectsEventsWaiting = new ArrayList<TrackedObjectsEvent>();
        this.liDarWorkerTracker = LiDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent detectObjectsEvent) -> { 
            // when we receive a DetectObjectsEvent, we process the data and add the TrackedObjectsEvent to the waiting list to be sent later at a delayed time
            List<TrackedObject> trackedObjects = liDarWorkerTracker.processData(detectObjectsEvent);
            for (TrackedObject trackedObject : trackedObjects) {
                StatisticalFolder.getInstance().incrementNumDetectedObjects();
            }
            trackedObjectsEventsWaiting.add(new TrackedObjectsEvent(trackedObjects, detectObjectsEvent.getTickTime()));
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            if (broadcast.getSenderClass().equals(TimeService.class)) {
                liDarWorkerTracker.terminate();
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast Crash) -> {
            liDarWorkerTracker.terminate();
            terminate();
        });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            Iterator<TrackedObjectsEvent> iterator = trackedObjectsEventsWaiting.iterator();
            while (iterator.hasNext()) {
                TrackedObjectsEvent trackedObjectsEvent = iterator.next();
                for (TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjects()) {
                    if (trackedObject.getId() == "ERROR") {
                        liDarWorkerTracker.crash();
                        sendBroadcast(new CrashedBroadcast(trackedObject.getDescription(), this.getName()));
                        terminate();
                        break;
                    }
                }
                if (tick.getTick() >= trackedObjectsEvent.getReceivedTick() + liDarWorkerTracker.getFrequency()) {
                    iterator.remove();
                    // System.out.println(PURPLE + "LiDarService: Sending TrackedObjectsEvent " + trackedObjectsEvent.getTrackedObjects() + RESET);
                    sendEvent(trackedObjectsEvent);
                    FusionSlam.getInstance().updateDataPassedBySensors("LidarWorker-" + liDarWorkerTracker.getId(), trackedObjectsEvent.getTrackedObjects());
                    if (LiDarDataBase.getInstance().isFinished(tick.getTick(), liDarWorkerTracker.getFrequency())) {
                        liDarWorkerTracker.terminate();
                        terminate();
                    }
                }
            }
        });
        FusionSlam.getInstance().IncrementNumOfActiveSensor();
    }
}
