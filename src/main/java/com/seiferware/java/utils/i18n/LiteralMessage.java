package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link Message} implementation that signals that it is meant to be used literally, and not as a reference to a
 * message which might (or might not) be translated. This is useful when some part of a system is expecting a {@link
 * Message} but in some unusual circumstances a text string needs to be used literally.
 */
public class LiteralMessage implements Message {
	private final String text;
	public LiteralMessage(String text) {
		this.text = text;
	}
	@Override
	public @NotNull String getValue() {
		return text;
	}
}
