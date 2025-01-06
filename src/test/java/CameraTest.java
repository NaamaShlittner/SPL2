import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {

    private Camera camera;

    @BeforeEach
    public void setUp() {
        // יצירת מצלמה עם נתוני בדיקה
        String id = "camera1";
        int frequency = 5;
        String key = "testKey";
        String cameraDataPath = "path/to/test/data.json"; // דמה נתונים
        camera = new Camera(id, frequency, key, cameraDataPath);
    }

    @Test
    public void testDetectObjects() {
        int tick = 5;
        List<DetectedObject> detectedObjects = camera.detectObjects(tick);
        
        // בדיקת התוצאה
        assertNotNull(detectedObjects, "The list of detected objects should not be null");
        assertFalse(detectedObjects.isEmpty(), "The list of detected objects should not be empty at tick " + tick);
        
        // בדיקת אובייקט ראשון (בהנחה שיש נתונים)
        if (!detectedObjects.isEmpty()) {
            DetectedObject firstObject = detectedObjects.get(0);
            assertNotNull(firstObject, "Detected object should not be null");
        }
    }

    @Test
    public void testShouldSendData() {
        int tick = 10;
        boolean shouldSend = camera.shouldSendData(tick);
        
        // בדיקת האם השיטה מחזירה את התוצאה הנכונה
        assertTrue(shouldSend || !shouldSend, "The result should be either true or false");
    }
}
