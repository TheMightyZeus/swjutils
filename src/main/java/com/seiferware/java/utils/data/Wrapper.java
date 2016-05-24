package com.seiferware.java.utils.data;

import org.jetbrains.annotations.NotNull;

/**
 * A very simple class designed to simply hold a single value. Useful when a variable needs to be final but its value
 * needs to be mutable.
 *
 * @param <T>
 * 		The type of object stored by this instance.
 */
public class Wrapper<T> {
	private @NotNull T value;
	public Wrapper(@NotNull T value) {
		this.value = value;
	}
	public @NotNull T getValue() {
		return value;
	}
	public void setValue(@NotNull T value) {
		this.value = value;
	}
}
