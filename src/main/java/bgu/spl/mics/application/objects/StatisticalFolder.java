package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StatisticalFolder is a singleton class responsible for aggregating key metrics.
 */
public class StatisticalFolder {
    private AtomicInteger systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;

    // Private constructor to prevent instantiation
    private StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    // Singleton instance holder
    private static class StatisticalFolderHolder {
        private static final StatisticalFolder INSTANCE = new StatisticalFolder();
    }

    // Public method to provide access to the singleton instance
    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.INSTANCE;
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime.compareAndSet(this.systemRuntime.get(), systemRuntime);
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public void incrementNumDetectedObjects() {
        this.numDetectedObjects.incrementAndGet();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public void incrementNumTrackedObjects() {
        this.numTrackedObjects.incrementAndGet();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void incrementNumLandmarks() {
        this.numLandmarks.incrementAndGet();
    }
}
