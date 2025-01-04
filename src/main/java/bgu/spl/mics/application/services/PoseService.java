package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;


/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    private GPSIMU gpsimu;
    final String PURPLE = "\033[35m";
    final String RESET = "\033[0m";
    final String BLUE = "\033[34m";
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            PoseEvent e = new PoseEvent(gpsimu.getPoseAtTime(tick.getTick()));
                System.out.println(PURPLE + "PoseService sending PoseEvent at tick " + tick.getTick() + RESET);
                sendEvent(e);
                if (gpsimu.getPoseAtTime(tick.getTick() + 1) == null) {
                    sendBroadcast(new TerminatedBroadcast());
                    System.err.println(BLUE +  getName() +" Sent Terminated Broadcast." + RESET);
                }
        });
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            terminate();
        });
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast Crash) -> {
            System.out.println(("Sad Times :(")); // sus line O_o wtf should we do here
        });
    }
}
