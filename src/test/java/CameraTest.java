
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    public void setUp() {
        String cameraDataPath = Paths.get("example input/camera_data.json").toAbsolutePath().toString();
        camera = new Camera("1", 10, "camera1",cameraDataPath);
    }

    @Test
    public void testDetectObjects() {
        List<DetectedObject> detectedObjects = camera.detectObjects(2);

        assertNotNull(detectedObjects);
    }

}
