package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private String id;
    private int frequency;
    private STATUS status = STATUS.UP;
    private List<TrackedObject> lastTrackedObjects;
    private LiDarDataBase dataBase;
    private MicroService fusionSLAMService;
    private Lock lock = new ReentrantLock();

    public LiDarWorkerTracker(String id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.dataBase = LiDarDataBase.getInstance();
    }

    public List<TrackedObject> processData(DetectObjectsEvent detectObjectsEvent) {
        lock.lock();
        try {
            List<TrackedObject> trackedObjects = dataBase.getTrackedObjects(detectObjectsEvent);
            lastTrackedObjects = trackedObjects; // Update the last tracked objects
            return trackedObjects;
        } finally {
            lock.unlock();
        }
    }

    // Getters and setters for the new fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }

    public String toString(){
        return "LiDarWorkerTracker{" +
                "id='" + id + '\'' +
                ", frequency=" + frequency +
                ", status=" + status +
                '}';
    }

    public void printDataBase(){
        System.out.println(dataBase);
    }
}
