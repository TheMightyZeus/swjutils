package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;

/**
 * A functional interface for parsing placeholders in messages. Used by {@link MessageParser}.
 */
public interface PlaceholderParser {
	/**
	 * Parses the placeholder and returns the resulting string.
	 *
	 * @param request
	 * 		The information about the particular placeholder, and message in whole.
	 *
	 * @return The resulting string.
	 * @throws PlaceholderException
	 * 		If the placeholder arguments or input are somehow invalid.
	 */
	@NotNull String parseObject(@NotNull PlaceholderRequest request) throws PlaceholderException;
}
