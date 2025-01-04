package bgu.spl.mics;

/**
 * a callback is a function designed to be called when a message is received.
 */
public interface Callback<T> {

    /**
     * This method is called when the micro-service has received a message that
     * matches its type.
     * @param c The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    void call(T c);
}
