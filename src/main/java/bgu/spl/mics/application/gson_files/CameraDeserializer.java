package bgu.spl.mics.application.gson_files;
import com.google.gson.*;
import java.lang.reflect.Type;
import bgu.spl.mics.application.objects.Camera;

public class CameraDeserializer implements JsonDeserializer<Camera> {
    private String cameraDataPath;
    public CameraDeserializer(String cameraDataPath) {
        this.cameraDataPath = cameraDataPath;
    }

    @Override
    public Camera deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        int frequency = jsonObject.get("frequency").getAsInt();
        String key = jsonObject.get("camera_key").getAsString();
        return new Camera(id, frequency, key, cameraDataPath);
    }
}
