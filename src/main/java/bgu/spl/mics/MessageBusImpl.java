package bgu.spl.mics;
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
	private static MessageBusImpl instance= new MessageBusImpl();
	private ConcurrentMap<Class<?>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
	private ConcurrentMap<Class<?>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;
	private ConcurrentMap<MicroService, ConcurrentLinkedQueue<Message>> messageQueues;
	private ConcurrentMap<Event<?>, Future<?>> eventFutures;
	private AtomicInteger roundRobinIndex;

	private MessageBusImpl() {
		eventSubscribers = new ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>>();
		broadcastSubscribers = new ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<MicroService>>();
		messageQueues = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>>();
		eventFutures = new ConcurrentHashMap<Event<?>, Future<?>>();
		roundRobinIndex = new AtomicInteger(0);
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
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> subscribers = (List<MicroService>)broadcastSubscribers.get(b.getClass()); // sus casting O_o
		if (subscribers != null) {
			for (MicroService m : subscribers) {
				messageQueues.get(m).add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		List<MicroService> subscribers = (List<MicroService>)eventSubscribers.get(e.getClass());
		if (subscribers == null || subscribers.isEmpty()) {
			return null;
		}
		MicroService m = getNextMicroService(subscribers);
		if (m == null) {
			return null;
		}
		Future<T> future = new Future<>();
		eventFutures.put(e, future);
		messageQueues.get(m).add(e);
		return future;
	}

	private <T> MicroService getNextMicroService(List<MicroService> subscribers) {
		int currentIndex = roundRobinIndex.getAndIncrement() % subscribers.size();
		return subscribers.get(currentIndex);
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
