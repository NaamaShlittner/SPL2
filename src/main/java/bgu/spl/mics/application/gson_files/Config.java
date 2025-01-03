package bgu.spl.mics.application.gson_files;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class Config {
    List<Camera> cameras;
    List<LiDarWorkerTracker> LiDarWorkers;
    int tickTime;
    int duration;
    GPSIMU gpsimu;
    LiDarDataBase lidarDataBase;

    private Config(List<Camera> cameras, List<LiDarWorkerTracker> LiDarWorkers, int tickTime, int duration, GPSIMU gpsimu) {
        this.cameras = cameras;
        this.LiDarWorkers = LiDarWorkers;
        this.tickTime = tickTime;
        this.duration = duration;
        this.gpsimu = gpsimu;
    }

    public static Config parseInputConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            Path path = Paths.get(filePath);
            String cameraDataPath = root.getAsJsonObject("Cameras").get("camera_datas_path").getAsString();
            String lidarDataPath = root.getAsJsonObject("LiDarWorkers").get("lidars_data_path").getAsString();
            Path cameraDataPathFull = path.getParent().resolve(cameraDataPath);
            Path lidarDataPathFull = path.getParent().resolve(lidarDataPath);
            String poseFilePath = root.get("poseJsonFile").getAsString();
            gsonBuilder.registerTypeAdapter(Camera.class, new CameraDeserializer(cameraDataPathFull.toString()));
            gsonBuilder.registerTypeAdapter(LiDarWorkerTracker.class, new LiDarDeserializer());
            Path poseFilePathFull = path.getParent().resolve(poseFilePath);
            gson = gsonBuilder.create();
            LiDarDataBase.getInstance(lidarDataPathFull.toString()); // load LiDar data and initialize the singleton instance
            List<Camera> cameras = gson.fromJson(
                    root.getAsJsonObject("Cameras").getAsJsonArray("CamerasConfigurations"),
                    new TypeToken<List<Camera>>() {}.getType());
            List<LiDarWorkerTracker> LiDarWorker = gson.fromJson(
                    root.getAsJsonObject("LiDarWorkers").getAsJsonArray("LidarConfigurations"),
                    new TypeToken<List<LiDarWorkerTracker>>() {}.getType());
            GPSIMU gpsimu = new GPSIMU(poseFilePathFull.toString());
            int tickTime = root.get("TickTime").getAsInt();
            int duration = root.get("Duration").getAsInt();
            Config config = new Config(cameras, LiDarWorker, tickTime, duration, gpsimu);
            // System.err.println(config); // for debugging
            return config;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // for debugging
    public String toString() {
        return "Config{" +
                "cameras=" + cameras.toString() +
                ", LiDarWorkers=" + LiDarWorkers.toString() +
                ", tickTime=" + tickTime +
                ", duration=" + duration +
                ", gpsimu=" + gpsimu.toString() +
                '}';
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public List<LiDarWorkerTracker> getLiDarWorkers() {
        return LiDarWorkers;
    }

    public int getTickTime() {
        return tickTime;
    }

    public int getDuration() {
        return duration;
    }

    public GPSIMU getGpsimu() {
        return gpsimu;
    }
}
