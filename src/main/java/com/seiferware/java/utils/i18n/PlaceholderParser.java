package com.seiferware.java.utils.i18n;

/**
 * A functional interface for parsing placeholders in messages. Used by
 * {@link MessageParser}.
 */
public interface PlaceholderParser {
	/**
	 * Parses the placeholder and returns the resulting string.
	 * 
	 * @param request
	 *            The information about the particular placeholder, and message
	 *            in whole.
	 * @return The resulting string.
	 */
	public String parseObject(PlaceholderRequest request);
}
