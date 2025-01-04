package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int TickTime;
    private int Duration;

    // color coding for debugging
	final String GREEN = "\033[32m";
    final String BLUE = "\033[34m";
    final String RED = "\033[31m";
    final String RESET = "\033[0m";
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast broadcast) -> {
            terminate();
        });
        for (int i = 0; i < Duration; i++) {
            try {
                System.err.println(BLUE + "Sent tick Broadcast: " + (i+1) + RESET);
                sendBroadcast(new TickBroadcast(i+1));
                Thread.sleep(TickTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendBroadcast(new TerminatedBroadcast());
        System.err.println(BLUE +  getName() +" Sent Terminated Broadcast." + RESET);
        terminate();
    }
}
