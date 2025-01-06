import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

public class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();
    }

    @Test
    public void testTransformToGlobalCoordinates() {
        Pose pose = new Pose(5.0f, 10.0f, 30,0);
        List<CloudPoint> localCoordinates = new ArrayList<>();
        localCoordinates.add(new CloudPoint(2.0f, 3.0f));

        List<CloudPoint> globalCoordinates = fusionSlam.transformToGlobalCoordinates(localCoordinates, pose);

        assertEquals(1, globalCoordinates.size());
        CloudPoint globalPoint = globalCoordinates.get(0);
        assertEquals(5.2320f, globalPoint.getX(), 0.0001);
        assertEquals(13.5980f, globalPoint.getY(), 0.0001);
    }

    @Test
    public void testUpdateTrackedObjects() {
        Pose pose = new Pose(5.0f, 10.0f, 30,1);
        fusionSlam.updatePose(pose);

        ArrayList<CloudPoint> coordinates = new ArrayList<>();
        coordinates.add(new CloudPoint(2.0f, 3.0f));
        TrackedObject trackedObject = new TrackedObject("1", 1, "Test Object", coordinates);

        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        fusionSlam.updateTrackedObjects(trackedObjects);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size());
        LandMark landmark = landmarks.get(0);
        assertEquals("1", landmark.getId());
        assertEquals("Test Object", landmark.getDescription());
        assertEquals(1, landmark.getCoordinates().size());
        CloudPoint globalPoint = landmark.getCoordinates().get(0);
        assertEquals(5.2320f, globalPoint.getX(), 0.0001);
        assertEquals(13.5980f, globalPoint.getY(), 0.0001);
    }
}