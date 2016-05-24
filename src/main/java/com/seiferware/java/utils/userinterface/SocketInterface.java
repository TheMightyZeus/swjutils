package com.seiferware.java.utils.userinterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An interface that is backed by a network connection.
 */
public class SocketInterface extends BufferedTextInterface {
	Socket socket = null;
	/**
	 * Creates the interface around a socket.
	 *
	 * @param socket
	 * 		The socket used to interact with the user.
	 *
	 * @throws IOException
	 * 		If an I/O error occurs when creating the input or output stream, the socket is closed, the socket is not
	 * 		connected, or the socket input has been shutdown using {@link Socket#shutdownInput() shutdownInput()}.
	 */
	public SocketInterface(@NotNull Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
		this.socket = socket;
	}
	@Override
	public void close() {
		super.close();
		try {
			socket.close();
		} catch (IOException ignored) {
		}
	}
	/**
	 * Gets the remote IP address the underlying socket is connected to.
	 *
	 * @return The remote address.
	 */
	@NotNull
	public String getRemoteAddress() {
		return socket.getInetAddress().toString();
	}
	@Override
	public @Nullable String readLine() {
		String s = super.readLine();
		if(s == null || s.length() < 3) {
			return s;
		}
		while(s.length() >= 3 && s.charAt(0) == (char)65533 && s.charAt(1) == (char)65533) {
			s = s.substring(3);
		}
		return s;
	}
	@Override
	public void send(@NotNull byte[] data) {
		try {
			socket.getOutputStream().write(data);
			out.flush();
		} catch (IOException ignored) {
		}
	}
	@Override
	public void send(@NotNull String data) {
		send(data.getBytes(StandardCharsets.US_ASCII));
	}
	@Override
	public void sendLine(@NotNull String data) {
		try {
			socket.getOutputStream().write(data.getBytes(StandardCharsets.US_ASCII));
			out.newLine();
			out.flush();
		} catch (IOException ignored) {
		}
	}
}
