package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Pose;

import java.util.List;

public class CrashedBroadcast implements Broadcast {
    private final String crashDetails;
    private final String faultySensor;
    private Object lastFrames;
    private List<Pose> poses;
    private MicroService sender;

    public CrashedBroadcast(String crashDetails, String faultySensor, Object lastFrames, List<Pose> poses) {
        this.crashDetails = crashDetails;
        this.faultySensor = faultySensor;
        this.lastFrames = lastFrames;
        this.poses = poses;
    }
    public CrashedBroadcast(String crashDetails, String faultySensor) {
        this.crashDetails = crashDetails;
        this.faultySensor = faultySensor;
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

    public String toString() {
        return "CrashedBroadcast: sent by= " + sender.getName() +", "  + crashDetails + ", " + faultySensor + ", " + lastFrames + ", " + poses.toString();
    }
}
