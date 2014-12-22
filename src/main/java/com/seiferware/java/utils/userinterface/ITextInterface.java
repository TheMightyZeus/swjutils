package com.seiferware.java.utils.userinterface;

import java.io.Closeable;

/**
 * Represents textual input and ouput to a single user.
 */
public interface ITextInterface extends Closeable, AutoCloseable {
	/**
	 * Sends the text to the user.
	 * 
	 * @param data
	 *            The text to send.
	 */
	void send(String data);
	/**
	 * Sends the text to the user, followed by a line separator.
	 * 
	 * @param data
	 *            The text to send.
	 */
	void sendLine(String data);
	/**
	 * Reads a line of input text. May block depending on implementation.
	 * 
	 * @return The first unread line of input, or {@code null} if there is no
	 *         line and the implementation is non-blocking.
	 */
	String readLine();
	/**
	 * Whether the input source has a line available to read.
	 * 
	 * @return {@code true} if the user has sent a line of input that has not
	 *         yet been read.
	 */
	boolean hasLineToRead();
	/**
	 * Whether the interface is currently active. The meaning of active depends
	 * on implementation.
	 * 
	 * @return {@code true} if the interface is ready to receive input and send
	 *         output.
	 */
	boolean isActive();
	@Override
	void close();
}