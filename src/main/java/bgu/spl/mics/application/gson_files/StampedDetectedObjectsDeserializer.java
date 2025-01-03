package bgu.spl.mics.application.gson_files;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class StampedDetectedObjectsDeserializer implements JsonDeserializer<StampedDetectedObjects> {

    public static List<StampedDetectedObjects> getCameraData(String cameraDataPath , String key){
        try (FileReader reader = new FileReader(cameraDataPath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            gsonBuilder.registerTypeAdapter(StampedDetectedObjects.class, new StampedDetectedObjectsDeserializer());
            gson = gsonBuilder.create();
            List<StampedDetectedObjects> stampedDetectedObjects = gson.fromJson(
                    root.getAsJsonArray(key), new TypeToken<List<StampedDetectedObjects>>() {}.getType());
            return stampedDetectedObjects;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public StampedDetectedObjects deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DetectedObject.class, new DetectedObjectDeserializer());
        Gson gson = gsonBuilder.create();
        int time = jsonObject.get("time").getAsInt();
        List<DetectedObject> detectedObjects = gson.fromJson(
                jsonObject.getAsJsonArray("detectedObjects"),
                new TypeToken<List<DetectedObject>>() {}.getType());
        return new StampedDetectedObjects(time, detectedObjects);
    }
    
}
