package com.seiferware.java.utils.data.store;

/**
 * 
 */
public class MismatchedBookmarkException extends RuntimeException {
	public MismatchedBookmarkException() {
	}
	public MismatchedBookmarkException(String message) {
		super(message);
	}
	public MismatchedBookmarkException(String message, Throwable cause) {
		super(message, cause);
	}
	public MismatchedBookmarkException(Throwable cause) {
		super(cause);
	}
}
