package com.seiferware.java.utils.userinterface;

import java.io.IOException;
import java.net.Socket;

/**
 * An interface that is backed by a network connection.
 */
public class SocketInterface extends BufferedTextInterface {
	Socket socket = null;
	
	/**
	 * Creates the interface around a socket.
	 * 
	 * @param socket
	 *            The socket used to interact with the user.
	 * @throws IOException
	 *             If an I/O error occurs when creating the input or output
	 *             stream, the socket is closed, the socket is not connected, or
	 *             the socket input has been shutdown using
	 *             {@link Socket#shutdownInput() shutdownInput()}.
	 */
	public SocketInterface(Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
		this.socket = socket;
	}
	@Override
	public void close() {
		super.close();
		try {
			socket.close();
		} catch(IOException e) {}
	}
	/**
	 * Gets the remote IP address the underlying socket is connected to.
	 * 
	 * @return The remote address.
	 */
	public String getRemoteAddress() {
		return socket.getInetAddress().toString();
	}
}
