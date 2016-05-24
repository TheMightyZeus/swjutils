package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * A provider for translating and parsing messages. It deals with translation, and uses a {@link MessageParser} to
 * handle placeholders.
 */
public interface MessageSource {
	/**
	 * Gets the default locale for this instance. Defaults to {@link Locale#getDefault()}.
	 *
	 * @return The locale.
	 */
	@NotNull Locale getDefaultLocale();
	/**
	 * Assigns a default locale to this instance.
	 *
	 * @param defaultLocale
	 * 		The locale.
	 */
	void setDefaultLocale(@NotNull Locale defaultLocale);
	/**
	 * Gets the message parser associated with this instance. The message parser is responsible for filling in
	 * placeholders.
	 *
	 * @return The message parser.
	 */
	@NotNull MessageParser getMessageParser();
	/**
	 * Sets the message parser to use with this instance.
	 *
	 * @param messageParser
	 * 		The message parser.
	 */
	void setMessageParser(@NotNull MessageParser messageParser);
	/**
	 * Translates and fulfills placeholders on the provided message. Equivalent to {@code parse(id, data,
	 * getDefaultLocale())}.
	 *
	 * @param id
	 * 		The message to translate.
	 * @param data
	 * 		The data for the placeholders, if any.
	 *
	 * @return The translated and parsed string.
	 */
	@NotNull
	default String parse(@NotNull Message id, @Nullable ArgMap data) throws MessageException {
		return parse(id, data, getDefaultLocale());
	}
	/**
	 * Translates and fulfills placeholders on the provided message, using the provided locale.
	 *
	 * @param id
	 * 		The message to translate.
	 * @param data
	 * 		The data for the placeholders, if any.
	 * @param locale
	 * 		The locale to use.
	 *
	 * @return The translated and parsed string.
	 */
	@NotNull
	default String parse(@NotNull Message id, @Nullable ArgMap data, @NotNull Locale locale) throws MessageException {
		return parse(translateIfNecessary(id, locale), data, locale);
	}
	/**
	 * Fulfills placeholders on the provided text. Equivalent to {@code parse(msg, data, getDefaultLocale())}.
	 *
	 * @param msg
	 * 		The text to parse.
	 * @param data
	 * 		The placeholder data.
	 *
	 * @return The parsed string.
	 */
	@NotNull
	default String parse(@NotNull String msg, @Nullable ArgMap data) throws MessageException {
		return parse(msg, data, getDefaultLocale());
	}
	/**
	 * Fulfills placeholders on the provided text, using the provided locale.
	 *
	 * @param msg
	 * 		The text to parse.
	 * @param data
	 * 		The placeholder data.
	 * @param locale
	 * 		The locale used in parsing, if needed.
	 *
	 * @return The parsed string.
	 */
	@NotNull
	default String parse(@NotNull String msg, @Nullable ArgMap data, @NotNull Locale locale) throws MessageException {
		return getMessageParser().parse(msg, data, locale, this);
	}
	/**
	 * Translates the message using the implementation-specific logic.
	 *
	 * @param id
	 * 		The message to translate.
	 * @param locale
	 * 		The locale to use for translation.
	 *
	 * @return The translated string, which may contain placeholders.
	 */
	@NotNull String translate(@NotNull Message id, @NotNull Locale locale) throws TranslationException;
	/**
	 * Translates the message using the implementation-specific logic, or in the case of an instance of {@link
	 * LiteralMessage}, returns {@code id.getValue()} directly.
	 *
	 * @param id
	 * 		The message to translate.
	 * @param locale
	 * 		The locale to use for translation.
	 *
	 * @return The translated string, which may contain placeholders.
	 */
	@NotNull
	default String translateIfNecessary(@NotNull Message id, @NotNull Locale locale) throws TranslationException {
		if(id instanceof LiteralMessage) {
			return id.getValue();
		}
		return translate(id, locale);
	}
}
