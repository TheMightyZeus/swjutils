package com.seiferware.java.utils.event.userinterface;

import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a user interface has been disconnected, closed, or is
 * otherwise no longer available for IO.
 */
public class InterfaceClosedEvent extends UserInterfaceEvent {
	/**
	 * Create the event.
	 * 
	 * @param target
	 *            The interface which has been closed.
	 */
	public InterfaceClosedEvent(@NotNull Object target) {
		super(target);
	}
}
