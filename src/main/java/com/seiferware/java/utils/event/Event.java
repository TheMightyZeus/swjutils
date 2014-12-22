package com.seiferware.java.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The base class for all events. Static methods on this class are used to
 * register and unregister handlers.
 * 
 * @see Listener
 */
public class Event {
	private static final Map<Object, Set<Object>> listeners = new HashMap<Object, Set<Object>>();
	private final Object target;
	
	/**
	 * Creates the event. Usually subclasses should be instantiated instead.
	 * 
	 * @param target
	 *            The object that is tied to the event. Listeners on this object
	 *            will be notified when the event is fired.
	 */
	public Event(Object target) {
		this.target = target;
	}
	/**
	 * <p>
	 * Assigns a listener to an object. When {@code target} fires an event (that
	 * is, an event with {@code target} as its target is fired), any listeners
	 * registered will have the chance to be notified of the event.
	 * </p>
	 * <p>
	 * Listeners should have methods that implement {@link Listener}. See that
	 * class for more information.
	 * </p>
	 * 
	 * @param target
	 *            The target of the events to be listened for.
	 * @param listener
	 *            The object that will be notified of the events.
	 * @see Listener
	 */
	public static final void addListener(Object target, Object listener) {
		if(!listeners.containsKey(target)) {
			listeners.put(target, new HashSet<Object>());
		}
		listeners.get(target).add(listener);
	}
	/**
	 * Removes a listener from an object.
	 * 
	 * @param target
	 *            The target of the events to no stop listening for.
	 * @param listener
	 *            The object that should no longer be notified of the events.
	 * @see #addListener(Object, Object)
	 */
	public static final void removeListener(Object target, Object listener) {
		if(listeners.containsKey(target)) {
			listeners.get(target).remove(listener);
		}
	}
	/**
	 * Fires the event. Any registered listeners will be notified if they have
	 * matching methods which are annotated with {@link Listener}.
	 * 
	 * @see Listener
	 */
	public final void fire() {
		if(listeners.containsKey(target)) {
			Set<Object> mine = listeners.get(target);
			for(Object obj : mine) {
				Class<?> cls = obj.getClass();
				Method[] meths = cls.getMethods();
				for(Method meth : meths) {
					if(meth.getAnnotation(Listener.class) != null) {
						Class<?>[] types = meth.getParameterTypes();
						// eventHandler(Event event, Object target)...
						// OR swap [1] and [0] for eventHandler(Object target, Event event)...
						if(types.length == 2 && types[1].isInstance(target) && types[0].isAssignableFrom(this.getClass())) {
							try {
								meth.invoke(obj, this, target);
							} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
						}
					}
				}
			}
		}
	}
	/**
	 * Retrieves the target of the event. This should be the object that caused
	 * the event to fire, and will be passed to event listeners. This object is
	 * passed to {@link #Event(Object)}, {@link #addListener(Object, Object)},
	 * and {@link #removeListener(Object, Object)} as {@code target}.
	 * 
	 * @return The event's target.
	 */
	public Object getTarget() {
		return target;
	}
}
