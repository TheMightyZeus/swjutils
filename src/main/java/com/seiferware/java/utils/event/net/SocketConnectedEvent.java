package com.seiferware.java.utils.event.net;

import org.jetbrains.annotations.NotNull;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * An event to be fired when a remote host has connected to a {@link ServerSocket}, spawning a new {@link Socket}.
 */
public class SocketConnectedEvent extends SocketPoolEvent {
	/**
	 * Creates the event.
	 *
	 * @param target
	 * 		The socket pool responsible for the connection.
	 * @param socket
	 * 		The {@link Socket} to which the connection is bound.
	 */
	public SocketConnectedEvent(@NotNull Object target, @NotNull Socket socket) {
		super(target, socket);
	}
}
