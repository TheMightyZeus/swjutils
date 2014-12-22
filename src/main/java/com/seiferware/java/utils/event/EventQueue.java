package com.seiferware.java.utils.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A mechanism for catching events and then handling them when ready. Useful for
 * catching asynchronous events and handling them in a main loop, or vice versa.
 *
 * @param <E>
 *            The type of the {@link Event} to be listened for. Will catch all
 *            subclasses of {@code E}.
 * @param <T>
 *            The type of the target of the events to listen for. Will catch
 *            events with any subclass of {@code T} as their target.
 */
public class EventQueue<E extends Event,T> {
	protected Queue<E> queue = new ConcurrentLinkedQueue<E>();
	private Class<E> eventClass;
	private Class<T> targetClass;
	
	/**
	 * Creates the event queue, and informs it which classes to listen for.
	 * 
	 * @param eventClass
	 *            Only events that match this class, or a subclass of it will be
	 *            stored in the queue.
	 * @param targetClass
	 *            Only events whose targets match this class or a subclass of it
	 *            will be stored in the queue.
	 */
	public EventQueue(Class<E> eventClass, Class<T> targetClass) {
		this.eventClass = eventClass;
		this.targetClass = targetClass;
	}
	@Listener
	protected void eventFired(E event, Object target) {
		if(eventClass.isInstance(event) && targetClass.isInstance(target)) {
			queue.offer(event);
		}
	}
	/**
	 * Retrieves, but does not remove, the first event in the queue, or returns
	 * {@code null} if this queue is empty.
	 * 
	 * @return The head of this queue, or {@code null} if this queue is empty.
	 */
	public E peek() {
		return queue.peek();
	}
	/**
	 * Retrieves and removes the first event in the queue, or returns
	 * {@code null} if this queue is empty.
	 * 
	 * @return The head of this queue, or {@code null} if this queue is empty.
	 */
	public E poll() {
		return queue.poll();
	}
}
