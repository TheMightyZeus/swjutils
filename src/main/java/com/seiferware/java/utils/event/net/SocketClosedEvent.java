package com.seiferware.java.utils.event.net;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

/**
 * An event to be fired when the connection to a remote host on a {@link Socket} has been closed.
 */
public class SocketClosedEvent extends SocketPoolEvent {
	/**
	 * Creates the event.
	 *
	 * @param target
	 * 		The socket pool responsible for the connection.
	 * @param socket
	 * 		The {@link Socket} to which the connection was bound.
	 */
	public SocketClosedEvent(@NotNull Object target, @NotNull Socket socket) {
		super(target, socket);
	}
}
