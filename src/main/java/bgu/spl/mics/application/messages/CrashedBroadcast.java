package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Pose;

import java.util.List;

public class CrashedBroadcast implements Broadcast {
    private final String crashDetails;
    private final String faultySensor;
    private final Object lastFrames;
    private final List<Pose> poses;

    public CrashedBroadcast(String crashDetails, String faultySensor, Object lastFrames, List<Pose> poses) {
        this.crashDetails = crashDetails;
        this.faultySensor = faultySensor;
        this.lastFrames = lastFrames;
        this.poses = poses;
    }

    public String getCrashDetails() {
        return crashDetails;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public Object getLastFrames() {
        return lastFrames;
    }

    public List<Pose> getPoses() {
        return poses;
    }
}
