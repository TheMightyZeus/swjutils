package com.seiferware.java.utils.event.net;

import java.net.Socket;

import com.seiferware.java.utils.event.Event;

/**
 * An event category for socket pool events that are tied to individual sockets.
 * Subclasses provide more specifics regarding what actually occurred.
 * 
 * @see Event
 * @see SocketConnectedEvent
 * @see SocketClosedEvent
 */
public class SocketPoolEvent extends Event {
	protected final Socket socket;
	/**
	 * Creates the event.
	 * 
	 * @param target
	 *            The containing socket pool instance.
	 * @param socket
	 *            The socket instance on which the event occurred.
	 */
	public SocketPoolEvent(Object target, Socket socket) {
		super(target);
		this.socket = socket;
	}
	/**
	 * The socket instance on which the event occurred.
	 * 
	 * @return The {@link Socket}
	 */
	public Socket getSocket() {
		return socket;
	}
}
