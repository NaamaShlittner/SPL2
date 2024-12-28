package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private boolean isResolved = false;
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
		synchronized (this) {
			while (!isDone()) {
				try {
					wait();
				}catch (InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
			return result;
		}
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public void resolve (T result) {
		if (!isResolved) {
			this.result = result;
			this.isResolved = true;
			notifyAll();
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return isResolved;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		if (isResolved) {
			return result;
		}

		long waitTime = unit.toMillis(timeout);
		long endTime = System.currentTimeMillis() + waitTime;

		while (waitTime > 0 && !isResolved) {
			try {
				this.wait(waitTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			waitTime = endTime - System.currentTimeMillis(); // calculate remaining time
		}

		return isDone() ? result : null;
	}

}
