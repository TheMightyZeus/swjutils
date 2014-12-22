package com.seiferware.java.utils.event.userinterface;

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
	public InterfaceClosedEvent(Object target) {
		super(target);
	}
}
