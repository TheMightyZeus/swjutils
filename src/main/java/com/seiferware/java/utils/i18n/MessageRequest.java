package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Used by {@link MessageParser} and {@link MessageSource} to pass information about the message to be parsed.
 */
public class MessageRequest {
	protected ArgMap data = ArgMap.empty();
	private MessageSource messageSource = null;
	private Locale locale = Locale.getDefault();
	/**
	 * Creates and populates the request.
	 *
	 * @param data
	 * 		The map of objects which can be referenced by placeholders.
	 * @param locale
	 * 		The locale used to fetch the message from a resource bundle if necessary, and also used for formatting dates
	 * 		and such.
	 * @param messageSource
	 * 		The message source being used. Especially important if any placeholder objects are other messages, or get
	 * 		parsed into other messages.
	 */
	public MessageRequest(@Nullable ArgMap data, @NotNull Locale locale, @NotNull MessageSource messageSource) {
		if(data != null) {
			this.data = data.getUnmodifiable();
		}
		this.locale = locale;
		this.messageSource = messageSource;
	}
	/**
	 * Retrieves an object mapped for a placeholder.
	 *
	 * @param key
	 * 		The name of the placeholder.
	 *
	 * @return The object, or {@code null} if the key is not present.
	 */
	@Nullable
	public Object getData(String key) {
		return data.get(key);
	}
	/**
	 * Retrieves an unmodifiable view of the placeholder map.
	 *
	 * @return The unmodifiable map.
	 */
	@NotNull
	public ArgMap getData() {
		return data;
	}
	/**
	 * Retrieves the locale passed to the constructor.
	 *
	 * @return The locale.
	 */
	@NotNull
	public Locale getLocale() {
		return locale;
	}
	/**
	 * Retrieves the message source that was passed to the constructor.
	 *
	 * @return The message source.
	 */
	@NotNull
	public MessageSource getMessageSource() {
		return messageSource;
	}
}
