package com.seiferware.java.utils.event.net;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * An event to be fired when a remote host has connected to a
 * {@link ServerSocket}, spawning a new {@link Socket}.
 */
public class SocketConnectedEvent extends SocketPoolEvent {
	/**
	 * Creates the event.
	 * 
	 * @param target
	 *            The socket pool responsible for the connection.
	 * @param socket
	 *            The {@link Socket} to which the connection is bound.
	 */
	public SocketConnectedEvent(Object target, Socket socket) {
		super(target, socket);
	}
}
