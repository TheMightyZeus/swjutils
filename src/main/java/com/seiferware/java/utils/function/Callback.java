package com.seiferware.java.utils.function;

/**
 * A functional interface that accepts no arguments and returns no value. It is exactly equivalent to the {@link
 * Runnable} interface, but doesn't carry the same multithreading implication.
 */
@FunctionalInterface
public interface Callback {
	void call();
}
