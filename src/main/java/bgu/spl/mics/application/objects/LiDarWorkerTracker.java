package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private LiDarDataBase dataBase;
    private MicroService fusionSLAMService;
    private Lock lock = new ReentrantLock();

    public LiDarWorkerTracker(LiDarDataBase dataBase, MicroService fusionSLAMService) {
        this.dataBase = dataBase;
        this.fusionSLAMService = fusionSLAMService;
    }

    public List<TrackedObject> processData(DetectObjectsEvent detectObjectsEvent) {
        lock.lock();
        try {
            // ביצוע עיבוד המידע בצורה סינכרונית
            // אפשר לגשת ולשנות את המידע בצורה בטוחה
            List<TrackedObject> trackedObjects = dataBase.getTrackedObjects(detectObjectsEvent);
            return trackedObjects;
        } finally {
            lock.unlock();
        }
    }
}
