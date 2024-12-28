package bgu.spl.mics;

import jdk.javadoc.internal.doclets.toolkit.util.Utils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static final MessageBusImpl instance = new MessageBusImpl();
	private ConcurrentMap<Class<?>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
	private ConcurrentMap<Class<?>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;
	private ConcurrentMap<MicroService, ConcurrentLinkedQueue<Message>> messageQueues;
	private ConcurrentMap<Event<?>, Future<?>> eventFutures;

	private MessageBusImpl() {
		eventSubscribers = new ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>>();
		broadcastSubscribers = new ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>>();
		messageQueues = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		eventFutures = new ConcurrentHashMap<Event<?>, Future<?>>();
	}

	public static MessageBusImpl getInstance() {
		return instance;
	}

	// if type isn't in the hash map we add it to the hash map. then any ways just add m into it.
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<MicroService>()).add(m);
	}

	// if type isn't in the hash map we add it to the hash map. then any ways just add m into it.
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<MicroService>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>)eventFutures.get(e); // sus casting O_O
		if(future != null)
			future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
		if (subscribers != null) {
			for (MicroService m : subscribers) {
				messageQueues.get(m).add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> subscribers = eventSubscribers.get(e.getClass());
		if (subscribers == null || subscribers.isEmpty()) {
			return null;
		}
		MicroService m = getNextMicroService(subscribers);
		if (m == null) {
			return null;
		}
		Future<T> future = new Future<T>();
		ConcurrentLinkedQueue<Message> queue = messageQueues.get(m);
		eventFutures.put(e, future);
		queue.add(e);
		queue.notifyAll(); // notify all threads waiting on that queue
		return future;
	}

	private MicroService getNextMicroService(ConcurrentLinkedQueue<MicroService> subscribers) {
		MicroService chosenMS =  subscribers.poll();
		subscribers.add(chosenMS);
		return chosenMS;
	}

	@Override
	public void register(MicroService m) {
		messageQueues.putIfAbsent(m, new ConcurrentLinkedQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		messageQueues.remove(m);
		eventSubscribers.values().forEach(queue -> queue.remove(m));
		broadcastSubscribers.values().forEach(queue -> queue.remove(m));
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> queue = messageQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
			while (queue.isEmpty()) {
				try {
					queue.wait();
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			return queue.poll();
	}
}
