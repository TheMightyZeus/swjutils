package com.seiferware.java.utils.event.userinterface;

import org.jetbrains.annotations.NotNull;

/**
 * An event to be fired when a user interface has received textual input.
 */
public class ReceiveTextEvent extends UserInterfaceEvent {
	private final String text;
	/**
	 * Creates the event.
	 *
	 * @param target
	 * 		The user interface that received input.
	 * @param line
	 * 		The textual input that was received.
	 */
	public ReceiveTextEvent(@NotNull Object target, @NotNull String line) {
		super(target);
		text = line;
	}
	/**
	 * Retrieves the textual input that was the cause of the event.
	 *
	 * @return The text.
	 */
	@NotNull
	public String getText() {
		return text;
	}
}
