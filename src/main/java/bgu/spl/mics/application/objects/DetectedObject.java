package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    private static int idCounter = 0;
    private final int id;
    private final String description;

    public DetectedObject( String description) {
        this.id = idCounter;
        idCounter++;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
