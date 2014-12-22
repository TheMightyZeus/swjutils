package com.seiferware.java.utils.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.seiferware.java.utils.event.net.SocketClosedEvent;
import com.seiferware.java.utils.event.net.SocketConnectedEvent;
import com.seiferware.java.utils.event.net.SocketPoolEvent;

/**
 * <p>
 * This annotation indicates that a method should be called when matching events
 * occur. When an object is registered as a listener for an event, using
 * {@link Event#addListener(Object, Object)}, and the event is fired, the
 * listening object is checked for methods with this annotation.
 * </p>
 * <p>
 * The method is passed the {@link Event} as the first parameter, and the return
 * value of {@link Event#getTarget} as the second. Only methods which can accept
 * these two arguments will be called. It is therefore possible to have a single
 * object listen for multiple events from different types of objects and
 * intelligently handle which method is called for each.
 * </p>
 * <p>
 * For example, one object can listen for {@link SocketConnectedEvent} in one
 * method and {@link SocketClosedEvent} in another, by defining those specific
 * types as parameters. It could also listen for both in the same method by
 * accepting {@link SocketPoolEvent} as the first parameter to that method.
 * </p>
 * <p>
 * A listener method must be declared {@code public} and should declare a
 * {@code void} return type. It must accept two arguments, where the first
 * extends {@link Event} and the second extends {@link Object}. Any method that
 * does not follow these rules will never be called by {@link Event#fire()}.
 * (except the {@code void} return type, in which case the return value is
 * simply ignored)
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
}
