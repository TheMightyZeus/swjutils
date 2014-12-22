package com.seiferware.java.utils.data.store;

/**
 * Thrown by {@link DataStoreReader} when attempting to retrieve a value by
 * type, and the value associated with the key is of an incompatible type. How
 * lenient this is depends on the implementation. Some may be able to store a
 * numeric string and retrieve it as any numerical primitive type, but this
 * should not be relied upon. This differs from {@link EntryNotFoundException}
 * in that the key is present in the current context, but can't be converted to
 * the requested type.
 */
public class IncompatibleTypeException extends Exception {
	private static final long serialVersionUID = 4823760574780738586L;
	
	/**
	 * Constructs a new exception with {@code null} as its detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call
	 * to {@link #initCause}.
	 */
	public IncompatibleTypeException() {}
	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 *
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public IncompatibleTypeException(String message) {
		super(message);
	}
	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
	 * contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for exceptions that are little more than wrappers
	 * for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since 1.4
	 */
	public IncompatibleTypeException(Throwable cause) {
		super(cause);
	}
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since 1.4
	 */
	public IncompatibleTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
