package bgu.spl.mics.application.gson_files;

import java.lang.reflect.Type;

import com.google.gson.*;

import bgu.spl.mics.application.objects.DetectedObject;

public class DetectedObjectDeserializer implements JsonDeserializer<DetectedObject>{
    @Override
    public DetectedObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        String desc = jsonObject.get("description").getAsString();
        return new DetectedObject(id, desc);
    }
    
}
