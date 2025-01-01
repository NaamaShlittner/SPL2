package bgu.spl.mics.application.gson_files;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class Config {
    List<Camera> cameras;
    List<LiDarWorkerTracker> LiDarWorkers;
    int tickTime;
    int duration;
    String poseFilePath;

    private Config(List<Camera> cameras, List<LiDarWorkerTracker> LiDarWorkers, int tickTime, int duration, String poseFilePath) {
        this.cameras = cameras;
        this.LiDarWorkers = LiDarWorkers;
        this.tickTime = tickTime;
        this.duration = duration;
        this.poseFilePath = poseFilePath;
    }

    public static Config parseInputConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            String cameraDataPath = root.getAsJsonObject("Cameras").get("camera_datas_path").getAsString();
            String lidarDataPath = root.getAsJsonObject("LidarWorkers").get("lidars_data_path").getAsString();
            gsonBuilder.registerTypeAdapter(Camera.class, new CameraDeserializer(cameraDataPath));
            gsonBuilder.registerTypeAdapter(LiDarWorkerTracker.class, new LiDarDeserializer(lidarDataPath));
            gson = gsonBuilder.create();
            List<Camera> cameras = gson.fromJson(
                    root.getAsJsonObject("Cameras").getAsJsonArray("CamerasConfigurations"),
                    new TypeToken<List<Camera>>() {}.getType());
            List<LiDarWorkerTracker> LiDarWorker = gson.fromJson(
                    root.getAsJsonObject("LidarWorkers").getAsJsonArray("LidarConfigurations"),
                    new TypeToken<List<LiDarWorkerTracker>>() {}.getType());
            int tickTime = root.get("TickTime").getAsInt();
            int duration = root.get("Duration").getAsInt();
            String poseFilePath = root.get("poseJsonFile").getAsString();
            Config config = new Config(cameras, LiDarWorker, tickTime, duration, poseFilePath);
            System.err.println(config); // for debugging
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
                ", LiDarWorker=" + LiDarWorkers.toString() +
                ", tickTime=" + tickTime +
                ", duration=" + duration +
                ", poseFilePath='" + poseFilePath + '\'' +
                '}';
    }
}
