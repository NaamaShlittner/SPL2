
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    public void setUp() {
        String cameraDataPath = Paths.get("example_input/camera_data.json").toAbsolutePath().toString();
        camera = new Camera("1", 10, "camera1",cameraDataPath);
    }

    @Test
    public void testDetectObjects() {
        List<DetectedObject> detectedObjects = camera.detectObjects(2);

        assertNotNull(detectedObjects);
    }

}
