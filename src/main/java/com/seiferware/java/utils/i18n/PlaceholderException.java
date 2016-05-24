package com.seiferware.java.utils.i18n;

public class PlaceholderException extends MessageException {
	public PlaceholderException(String message, Exception inner) {
		super(message, inner);
	}
	public PlaceholderException(Exception inner) {
		super(inner);
	}
}
