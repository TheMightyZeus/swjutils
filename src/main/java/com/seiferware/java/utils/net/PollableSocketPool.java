package com.seiferware.java.utils.net;

import com.seiferware.java.utils.event.Event;
import com.seiferware.java.utils.event.EventQueue;
import com.seiferware.java.utils.event.net.SocketClosedEvent;
import com.seiferware.java.utils.event.net.SocketConnectedEvent;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;

/**
 * A version of {@link SocketPool} that doesn't throw events, but rather maintains a queue of opened and closed sockets,
 * which can later be polled at leisure.
 */
public class PollableSocketPool extends SocketPool {
	protected EventQueue<SocketConnectedEvent, SocketPool> connectQueue = new EventQueue<>(SocketConnectedEvent.class, SocketPool.class);
	protected EventQueue<SocketClosedEvent, SocketPool> disconnectQueue = new EventQueue<>(SocketClosedEvent.class, SocketPool.class);
	/**
	 * Creates the pool, ready to listen on the specified port.
	 *
	 * @param port
	 * 		The port on which to listen.
	 */
	public PollableSocketPool(int port) {
		super(port);
		Event.addListener(this, connectQueue);
		Event.addListener(this, disconnectQueue);
	}
	/**
	 * Retrieves the next closed connection, or {@code null} if no connections have been closed since the queue was
	 * emptied.
	 *
	 * @return The closed connection.
	 */
	@Nullable
	public Socket getClosedConnection() {
		while(disconnectQueue.peek() != null) {
			SocketClosedEvent ev = disconnectQueue.poll();
			if(ev != null) {
				return ev.getSocket();
			}
		}
		return null;
	}
	/**
	 * Retrieves the next new connection, or {@code null} if no connections have been opened since the queue was
	 * emptied.
	 *
	 * @return The opened connection.
	 */
	@Nullable
	public Socket getNewConnection() {
		while(connectQueue.peek() != null) {
			SocketConnectedEvent ev = connectQueue.poll();
			if(ev != null) {
				return ev.getSocket();
			}
		}
		return null;
	}
}
