package com.seiferware.java.utils.event.userinterface;

import com.seiferware.java.utils.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * An event to be fired when something occurs relating to a user interface.
 */
public class UserInterfaceEvent extends Event {
	/**
	 * Creates the event.
	 * 
	 * @param target
	 *            The user interface that triggered the event.
	 */
	public UserInterfaceEvent(@NotNull Object target) {
		super(target);
	}
}
