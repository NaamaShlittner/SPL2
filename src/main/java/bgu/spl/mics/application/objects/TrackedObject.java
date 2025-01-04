package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private ArrayList<CloudPoint> coordinates;

    public TrackedObject(String id, int time, String description, ArrayList<CloudPoint> coordinates) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public LandMark getLandmark() {
        return new LandMark(id, description, coordinates);
    }

    public Pose getPose() {
        return new Pose(time);
    }

    public String toString() {
        return "TrackedObject: id=" + id + ", desc=" + description + ", time=" + time + ", coordinates=" + coordinates.toString();
    }
}
