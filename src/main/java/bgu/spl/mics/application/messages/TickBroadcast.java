package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private final int tick;

    public TickBroadcast(int tick) {
        synchronized (this) {
            this.tick = tick;
        }
    }

    public int getTick() {
        synchronized (this) {
            return tick;
        }
    }

    public String toString() {
        return "TickBroadcast{" +
                "tick=" + tick +
                '}';
    }
}
