package bgu.spl.mics.application.gson_files;

import bgu.spl.mics.application.objects.Pose;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class PoseDeserializer implements JsonDeserializer<Pose> {
    @Override
    public Pose deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int time = jsonObject.get("time").getAsInt();
        float x = jsonObject.get("x").getAsFloat();
        float y = jsonObject.get("y").getAsFloat();
        float yaw = jsonObject.get("yaw").getAsFloat();
        return new Pose(x, y, yaw, time);
    }

    public static List<Pose> getPoseList(String poseFilePath) {
        try (FileReader reader = new FileReader(poseFilePath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Pose.class, new PoseDeserializer());
            Gson gson = gsonBuilder.create();
            List<Pose> poseList = gson.fromJson(reader, new TypeToken<List<Pose>>() {}.getType());
            return poseList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
