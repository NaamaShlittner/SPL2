package bgu.spl.mics.application.gson_files;
import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class Config {
    List<Camera> cameras;
    List<LiDarWorkerTracker> LiDarWorker;
    int tickTime;
    int duration;
    String poseFilePath;

    private Config(List<Camera> cameras, List<LiDarWorkerTracker> LiDarWorker, int tickTime, int duration, String poseFilePath) {
        this.cameras = cameras;
        this.LiDarWorker = LiDarWorker;
        this.tickTime = tickTime;
        this.duration = duration;
        this.poseFilePath = poseFilePath;
    }

    public static Config parseInputConfig(String filePath) {
        return null;   
    }
}
