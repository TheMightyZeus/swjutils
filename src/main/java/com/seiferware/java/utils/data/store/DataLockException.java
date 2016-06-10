package com.seiferware.java.utils.data.store;

public class DataLockException extends RuntimeException {
	public DataLockException() {
		super("The requested data store traversal would violate a lock which was previously placed.");
	}
	public DataLockException(String message) {
		super(message);
	}
	public DataLockException(String message, Throwable cause) {
		super(message, cause);
	}
	public DataLockException(Throwable cause) {
		super(cause);
	}
}
