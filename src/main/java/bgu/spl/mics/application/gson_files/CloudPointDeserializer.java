package bgu.spl.mics.application.gson_files;

import com.google.gson.*;
import java.lang.reflect.Type;
import bgu.spl.mics.application.objects.CloudPoint;

public class CloudPointDeserializer implements JsonDeserializer<CloudPoint> {
    @Override
    public CloudPoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        float x = jsonArray.get(0).getAsFloat();
        float y = jsonArray.get(1).getAsFloat();
        return new CloudPoint(x, y);
    }
    
}
