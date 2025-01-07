import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.CameraService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MessageBusTest {
    private MessageBusImpl messageBus;
    private MicroService microService;
    private Camera camera;
    @BeforeEach
    public void setUp() {
        messageBus = MessageBusImpl.getInstance();
        camera = new Camera(1, 10, "camera1");
        microService = new CameraService(camera);
        messageBus.register(microService);
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService);
        messageBus.subscribeBroadcast(TickBroadcast.class, microService);
    }

    @Test
    public void testSendEvent() {
        List<DetectedObject> detectedObjects = new ArrayList<>();
        detectedObjects.add(new DetectedObject("1","obj"));
        DetectObjectsEvent event = new DetectObjectsEvent(camera.getId(),detectedObjects, 0);
        Future<DetectedObject> future = messageBus.sendEvent(event);

        assertNotNull(future);
        assertFalse(future.isDone());
    }
    @Test
    public void testComplete() {
        List<DetectedObject> detectedObjects = new ArrayList<>();
        detectedObjects.add(new DetectedObject("1","obj"));
        DetectObjectsEvent event = new DetectObjectsEvent(camera.getId(),detectedObjects, 0);
        Future<DetectedObject> future = messageBus.sendEvent(event);
        messageBus.complete(event, null);

        assertTrue(future.isDone());
        assertNull(future.get());
    }
    
    @Test
    public void testSimpleBroadcast() {
        TickBroadcast broadcast = new TickBroadcast(1);
        messageBus.sendBroadcast(broadcast);
        try {
            Message message = messageBus.awaitMessage(microService);
            assertNotNull(message, "MicroService should receive the broadcast");
            assertTrue(message instanceof TickBroadcast, "Message should be a TickBroadcast");
            assertEquals(broadcast, message, "The received broadcast should match the sent broadcast");
        } catch (InterruptedException e) {
            fail("awaitMessage was interrupted");
        }
    }
}