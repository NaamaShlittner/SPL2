package bgu.spl.mics.application.gson_files;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.StampedCloudPoints;

public class StampedCloudPointsDeserializer implements JsonDeserializer<StampedCloudPoints>{
    
    public static List<StampedCloudPoints> getLidarData(String lidarDataPath){
        try (FileReader reader = new FileReader(lidarDataPath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(CloudPoint.class, new CloudPointDeserializer());
            Gson gson = gsonBuilder.create();
            JsonArray root = gson.fromJson(reader, JsonArray.class);
            return gson.fromJson(root, new TypeToken<List<StampedCloudPoints>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public StampedCloudPoints deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(CloudPoint.class, new CloudPointDeserializer());
        Gson gson = gsonBuilder.create();
        String id = jsonObject.get("id").getAsString();
        int time = jsonObject.get("time").getAsInt();
        JsonArray cloudPointsArray = jsonObject.get("cloudPoints").getAsJsonArray();
        List <CloudPoint> cloudPoints = gson.fromJson(cloudPointsArray, new TypeToken<List<CloudPoint>>() {}.getType());
        return new StampedCloudPoints(id, time, cloudPoints);
    }
}
