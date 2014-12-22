package com.seiferware.java.utils.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.seiferware.java.utils.event.net.SocketClosedEvent;
import com.seiferware.java.utils.event.net.SocketConnectedEvent;
import com.seiferware.java.utils.threading.AsyncTask;

/**
 * A utility which listens on a socket and throws events when connections are
 * opened or closed.
 */
public class SocketPool extends AsyncTask {
	ServerSocket socket;
	List<Socket> sockList = new ArrayList<Socket>();
	protected int port;
	protected AsyncTask disconnects;
	
	/**
	 * Creates the pool, ready to listen on the specified port.
	 * 
	 * @param port
	 *            The port on which to listen.
	 */
	public SocketPool(int port) {
		this.port = port;
		final SocketPool me = this;
		disconnects = new AsyncTask() {
			@Override
			protected void onLoop() {
				for(Socket sock : sockList.subList(0, sockList.size())) {
					if(!sock.isConnected() || sock.isClosed()) {
						sockList.remove(sock);
						new SocketClosedEvent(me, sock).fire();
					}
				}
			}
		};
		disconnects.start();
	}
	@Override
	protected void onStart() {
		try {
			socket = new ServerSocket();
			socket.bind(new InetSocketAddress(port));
		} catch(IOException e) {
			stop();
		}
	}
	@Override
	protected void onLoop() {
		Socket client = null;
		try {
			client = socket.accept();
			sockList.add(client);
			new SocketConnectedEvent(this, client).fire();
		} catch(IOException e) {}
	}
	@Override
	protected void onStop() {
		disconnects.stopAndBlock();
		for(Socket sock : sockList.subList(0, sockList.size())) {
			if(sock.isConnected() && !sock.isClosed()) {
				try {
					sock.close();
					new SocketClosedEvent(this, sock).fire();
					sockList.remove(sock);
				} catch(IOException e) {}
			}
		}
		if(socket != null) {
			try {
				socket.close();
			} catch(IOException e) {}
		}
	}
	@Override
	protected boolean requestStop() {
		if(socket != null) {
			try {
				socket.close();
			} catch(IOException e) {}
		}
		return true;
	}
}
