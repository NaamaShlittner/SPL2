package bgu.spl.mics.application.objects;

/**
 * StatisticalFolder is a singleton class responsible for aggregating key metrics.
 */
public class StatisticalFolder {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;

    // Private constructor to prevent instantiation
    private StatisticalFolder() {
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
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
