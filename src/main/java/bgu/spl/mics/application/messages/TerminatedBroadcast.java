package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    public TerminatedBroadcast(Class<?> c) {
        classOfSender = c;
    }
    private Class<?> classOfSender;

    public String toString() {
        return "TerminatedBroadcast from: " + classOfSender.getName();
    }

    public Class<?> getSenderClass() {
        return classOfSender;
    }
}
