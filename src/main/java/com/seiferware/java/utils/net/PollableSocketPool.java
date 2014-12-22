package com.seiferware.java.utils.net;

import java.net.Socket;

import com.seiferware.java.utils.event.Event;
import com.seiferware.java.utils.event.EventQueue;
import com.seiferware.java.utils.event.net.SocketClosedEvent;
import com.seiferware.java.utils.event.net.SocketConnectedEvent;

/**
 * A version of {@link SocketPool} that doesn't throw events, but rather
 * maintains a queue of opened and closed sockets, which can later be polled at
 * leisure.
 */
public class PollableSocketPool extends SocketPool {
	protected EventQueue<SocketConnectedEvent, Socket> connectQueue = new EventQueue<>(SocketConnectedEvent.class, Socket.class);
	protected EventQueue<SocketClosedEvent, Socket> disconnectQueue = new EventQueue<>(SocketClosedEvent.class, Socket.class);
	
	/**
	 * Creates the pool, ready to listen on the specified port.
	 * 
	 * @param port
	 *            The port on which to listen.
	 */
	public PollableSocketPool(int port) {
		super(port);
		Event.addListener(this, connectQueue);
		Event.addListener(this, disconnectQueue);
	}
	/**
	 * Retrieves the next new connection, or {@code null} if no connections have
	 * been opened since the queue was emptied.
	 * 
	 * @return The openedd connection.
	 */
	public Socket getNewConnection() {
		while(connectQueue.peek() != null) {
			Event ev = connectQueue.poll();
			if(ev instanceof SocketConnectedEvent) {
				return (Socket)ev.getTarget();
			}
		}
		return null;
	}
	/**
	 * Retrieves the next closed connection, or {@code null} if no connections
	 * have been opened since the queue was emptied.
	 * 
	 * @return The closed connection.
	 */
	public Socket getClosedConnection() {
		while(disconnectQueue.peek() != null) {
			Event ev = disconnectQueue.poll();
			if(ev instanceof SocketClosedEvent) {
				return (Socket)ev.getTarget();
			}
		}
		return null;
	}
}
