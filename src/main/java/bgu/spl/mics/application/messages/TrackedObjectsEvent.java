package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvent implements Event<List<TrackedObject>> {
    private List<TrackedObject> trackedObjects;
    private int receivedTick;


    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, int receivedTick) {
        this.trackedObjects = trackedObjects;
        this.receivedTick = receivedTick;
    }

    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    public int getReceivedTick() {
        return receivedTick;
    }

    public String toString() {
        return "TrackedObjectsEvent: " + trackedObjects.toString();
    }
}
