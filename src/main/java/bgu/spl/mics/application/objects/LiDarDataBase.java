package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static LiDarDataBase instance;
    private List<CloudPoint> cloudPoints;

    private LiDarDataBase(String filePath) {
        loadCloudPointsFromFile(filePath);
    }

    private void loadCloudPointsFromFile(String filePath) {
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }
        return instance;
    }
}
