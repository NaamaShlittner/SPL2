package bgu.spl.mics.application.gson_files;
import com.google.gson.*;
import java.lang.reflect.Type;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class LiDarDeserializer implements JsonDeserializer<LiDarWorkerTracker> {
    @Override
    public LiDarWorkerTracker deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        int frequency = jsonObject.get("frequency").getAsInt();
        return new LiDarWorkerTracker(id, frequency);
    }
    
}
