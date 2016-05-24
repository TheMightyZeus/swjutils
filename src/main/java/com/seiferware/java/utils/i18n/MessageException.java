package com.seiferware.java.utils.i18n;

public class MessageException extends Exception {
	public MessageException(String message, Exception inner) {
		super(message, inner);
	}
	public MessageException(Exception inner) {
		super(inner);
	}
}
