package com.seiferware.java.utils.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for all events. Static methods on this class are used to register and unregister handlers.
 *
 * @see Listener
 */
public class Event {
	private static final Map<Object, List<Object>> listeners = new HashMap<>();
	private static final Map<Class<?>, List<MethodReference>> listenerMethods = new HashMap<>();
	private final Object target;
	private final boolean bubbles;
	private final boolean cancellable;
	protected Object currentTarget;
	private boolean canceled = false;
	/**
	 * Creates the event. Usually subclasses should be instantiated instead.
	 *
	 * @param target
	 * 		The object that is tied to the event. Listeners on this object will be notified when the event is fired.
	 */
	public Event(@NotNull Object target) {
		this(target, true, true);
	}
	protected Event(@NotNull Object target, boolean bubbles, boolean canCancel) {
		this.target = target;
		this.currentTarget = target;
		this.bubbles = bubbles;
		this.cancellable = canCancel;
	}
	/**
	 * <p> Assigns a listener to an object. When {@code target} fires an event (that is, an event with {@code target}
	 * as
	 * its target is fired), any listeners registered will have the chance to be notified of the event. </p> <p>
	 * Listeners should have methods that implement {@link Listener}. See that class for more information. </p>
	 *
	 * @param target
	 * 		The target of the events to be listened for.
	 * @param listener
	 * 		The object that will be notified of the events.
	 *
	 * @see Listener
	 */
	public static void addListener(@NotNull Object target, @NotNull Object listener) {
		if(!listeners.containsKey(target)) {
			listeners.put(target, new ArrayList<>());
		}
		if(!listeners.get(target).contains(listener)) {
			listeners.get(target).add(listener);
		}
		Class<?> cls = listener.getClass();
		if(!listenerMethods.containsKey(cls)) {
			List<MethodReference> methods = new ArrayList<>();
			Method[] meths = ReflectionUtils.getUniqueDeclaredMethods(cls); //cls.getMethods();
			for(Method meth : meths) {
				if(meth.getAnnotation(Listener.class) != null) {
					Class<?>[] types = meth.getParameterTypes();
					if(types.length == 2 && Event.class.isAssignableFrom(types[0])) {
						MethodReference mr = new MethodReference();
						try {
							meth.setAccessible(true);
						} catch (SecurityException ignored) {
						}
						mr.setMethod(meth);
						mr.setEventParam(types[0].asSubclass(Event.class));
						mr.setTargetParam(types[1]);
						methods.add(mr);
					}
				}
			}
			listenerMethods.put(cls, methods);
		}
	}
	/**
	 * Removes a listener from an object.
	 *
	 * @param target
	 * 		The target of the events to no stop listening for.
	 * @param listener
	 * 		The object that should no longer be notified of the events.
	 *
	 * @see #addListener(Object, Object)
	 */
	public static void removeListener(@NotNull Object target, @NotNull Object listener) {
		if(listeners.containsKey(target)) {
			listeners.get(target).remove(listener);
		}
	}
	protected void bubble() {
		if(currentTarget != null && currentTarget instanceof EventTarget) {
			currentTarget = ((EventTarget) currentTarget).getBubbleParent();
			if(currentTarget != null) {
				fire();
			}
		}
	}
	/**
	 * Whether the event will bubble. If set to {@code true}, the event will be processed recursively for each target's
	 * {@link EventTarget#getBubbleParent()}, for targets which implement the interface.
	 *
	 * @return {@code true} if the event may be propagated to bubble parents, {@code false} otherwise.
	 */
	public boolean canBubble() {
		return bubbles;
	}
	/**
	 * Cancels the event if it is cancellable. Cancelled events will not fire on any objects further up the bubble
	 * chain.
	 */
	public void cancel() {
		if(cancellable && !canceled) {
			canceled = true;
		}
	}
	/**
	 * Fires the event. Any registered listeners will be notified if they have matching methods which are annotated
	 * with
	 * {@link Listener}.
	 *
	 * @see Listener
	 */
	public final void fire() {
		if(!canceled && currentTarget != null && listeners.containsKey(currentTarget)) {
			List<Object> mine = listeners.get(currentTarget);
			for(Object obj : mine) {
				Class<?> cls = obj.getClass();
				if(listenerMethods.containsKey(cls)) {
					listenerMethods.get(cls).stream().filter(mr -> mr.getEventParam().isAssignableFrom(this.getClass()) && mr.getTargetParam().isInstance(target)).forEach(mr -> {
						try {
							mr.getMethod().invoke(obj, this, target);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
						}
					});
				}
				//Method[] meths = ReflectionUtils.getUniqueDeclaredMethods(cls); //cls.getMethods();
				//for(Method meth : meths) {
				//	if(meth.getAnnotation(Listener.class) != null) {
				//		Class<?>[] types = meth.getParameterTypes();
				//		// eventHandler(Event event, Object target)...
				//		// OR swap [1] and [0] for eventHandler(Object target, Event event)...
				//		if(types.length == 2 && types[1].isInstance(target) && types[0].isAssignableFrom(this.getClass())) {
				//			try {
				//				meth.invoke(obj, this, target);
				//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
				//			}
				//		}
				//	}
				//}
			}
		}
		if(bubbles && !canceled) {
			bubble();
		}
	}
	/**
	 * Retrieves the current target the event is firing on. This may or may not be the object that caused the event to
	 * fire. If it is different from {@link #getTarget()}, it is further up in the bubble chain. This object is passed
	 * to {@link #Event(Object)}, {@link #addListener(Object, Object)}, and {@link #removeListener(Object, Object)} as
	 * {@code target}.
	 *
	 * @return The event's current firing target.
	 */
	@Nullable
	public Object getCurrentTarget() {
		return target;
	}
	/**
	 * Retrieves the target of the event. This should be the object that caused the event to fire, and will be passed
	 * to
	 * event listeners. This object is passed to {@link #Event(Object)}, {@link #addListener(Object, Object)}, and
	 * {@link #removeListener(Object, Object)} as {@code target}.
	 *
	 * @return The event's target.
	 */
	@NotNull
	public Object getTarget() {
		return target;
	}
	/**
	 * Whether the event has been canceled.
	 *
	 * @return {@code true} if the event has been cancelled, {@code false} otherwise.
	 */
	public boolean isCanceled() {
		return canceled;
	}
	/**
	 * Whether the event is eligible for cancellation.
	 *
	 * @return {@code true} if the event may be canceled, {@code false} otherwise.
	 */
	public boolean isCancellable() {
		return cancellable;
	}
	/**
	 * This is for caching the listener methods instead of iterating through every method of each class in the
	 * inheritance chain of each listener object when the event is fired.
	 * Adding this caching took a test that fired 100,000 events from ~10s to ~500ms.
	 */
	private static class MethodReference {
		private Method method;
		private Class<? extends Event> eventParam;
		private Class<?> targetParam;
		public Class<? extends Event> getEventParam() {
			return eventParam;
		}
		public void setEventParam(Class<? extends Event> eventParam) {
			this.eventParam = eventParam;
		}
		public Method getMethod() {
			return method;
		}
		public void setMethod(Method method) {
			this.method = method;
		}
		public Class<?> getTargetParam() {
			return targetParam;
		}
		public void setTargetParam(Class<?> targetParam) {
			this.targetParam = targetParam;
		}
	}
}
