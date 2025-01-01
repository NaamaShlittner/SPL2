package bgu.spl.mics.application.objects;

/**
 * The StatisticalFolder aggregates key metrics reflecting the system's performance. Both cameras
 * and LiDAR workers update this folder each time they send their observations, regardless of
 * whether the detections are new or re-detections. This ensures that the statistics accurately
 * represent all detected and tracked objects.
 */
public class StatisticalFolder {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;

    public StatisticalFolder() {
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
    }

    public int getSystemRuntime() {
        return systemRuntime;
    }

    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime = systemRuntime;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public void incrementNumDetectedObjects() {
        this.numDetectedObjects++;
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects;
    }

    public void incrementNumTrackedObjects() {
        this.numTrackedObjects++;
    }

    public int getNumLandmarks() {
        return numLandmarks;
    }

    public void incrementNumLandmarks() {
        this.numLandmarks++;
    }
}
