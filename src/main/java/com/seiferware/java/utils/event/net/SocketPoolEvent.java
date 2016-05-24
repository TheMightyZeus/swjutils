package com.seiferware.java.utils.event.net;

import com.seiferware.java.utils.event.Event;
import org.jetbrains.annotations.NotNull;

import java.net.Socket;

/**
 * An event category for socket pool events that are tied to individual sockets. Subclasses provide more specifics
 * regarding what actually occurred.
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
	 * 		The containing socket pool instance.
	 * @param socket
	 * 		The socket instance on which the event occurred.
	 */
	public SocketPoolEvent(@NotNull Object target, @NotNull Socket socket) {
		super(target);
		this.socket = socket;
	}
	/**
	 * The socket instance on which the event occurred.
	 *
	 * @return The {@link Socket}
	 */
	@NotNull
	public Socket getSocket() {
		return socket;
	}
}
