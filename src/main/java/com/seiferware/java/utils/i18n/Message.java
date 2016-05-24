package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

/**
 * A functional interface that serves to indicate that an object is to be parsed as a message in a message source.
 */
public interface Message {
	/**
	 * Used to determine the ID of the textual content of this message. When used with a resource bundle, this is the
	 * resource identifier.
	 *
	 * @return The message ID.
	 */
	@NotNull String getValue();
}
