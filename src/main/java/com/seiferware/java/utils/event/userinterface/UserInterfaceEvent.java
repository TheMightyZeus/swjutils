package com.seiferware.java.utils.event.userinterface;

import com.seiferware.java.utils.event.Event;

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
	public UserInterfaceEvent(Object target) {
		super(target);
	}
}
