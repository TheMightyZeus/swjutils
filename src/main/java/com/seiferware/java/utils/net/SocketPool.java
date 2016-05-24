package com.seiferware.java.utils.net;

import com.seiferware.java.utils.event.net.SocketClosedEvent;
import com.seiferware.java.utils.event.net.SocketConnectedEvent;
import com.seiferware.java.utils.threading.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility which listens on a socket and throws events when connections are opened or closed.
 */
public class SocketPool extends AsyncTask {
	protected int port;
	protected AsyncTask disconnects;
	ServerSocket socket;
	List<Socket> sockList = new ArrayList<>();
	/**
	 * Creates the pool, ready to listen on the specified port.
	 *
	 * @param port
	 * 		The port on which to listen.
	 */
	public SocketPool(int port) {
		this.port = port;
		final SocketPool me = this;
		disconnects = new AsyncTask() {
			@Override
			protected void onLoop() {
				new ArrayList<>(sockList).stream().filter(sock -> !sock.isConnected() || sock.isClosed()).forEach(sock -> {
					sockList.remove(sock);
					new SocketClosedEvent(me, sock).fire();
				});
			}
		};
		disconnects.start();
	}
	@Override
	protected void onLoop() {
		try {
			Socket client = socket.accept();
			sockList.add(client);
			new SocketConnectedEvent(this, client).fire();
		} catch (IOException ignored) {
		}
	}
	@Override
	protected void onStart() {
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			stop();
		}
	}
	@Override
	protected void onStop() {
		disconnects.stopAndBlock();
		new ArrayList<>(sockList).stream().filter(sock -> sock.isConnected() && !sock.isClosed()).forEach(sock -> {
			try {
				sock.close();
				new SocketClosedEvent(this, sock).fire();
				sockList.remove(sock);
			} catch (IOException ignored) {
			}
		});
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}
	@Override
	protected boolean requestStop() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
		return true;
	}
}
