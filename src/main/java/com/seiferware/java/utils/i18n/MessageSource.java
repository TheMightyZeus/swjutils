package com.seiferware.java.utils.i18n;

import java.util.Locale;

/**
 * A provider for translating and parsing messages. It deals with translation,
 * and uses a {@link MessageParser} to handle placeholders.
 */
public interface MessageSource {
	/**
	 * Gets the default locale for this instance. Defaults to
	 * {@link Locale#getDefault()}.
	 * 
	 * @return The locale.
	 */
	public Locale getDefaultLocale();
	/**
	 * Assigns a default locale to this instance.
	 * 
	 * @param defaultLocale
	 *            The locale.
	 */
	public void setDefaultLocale(Locale defaultLocale);
	/**
	 * Gets the message parser associated with this instance. The message parser
	 * is responsible for filling in placeholders.
	 * 
	 * @return The message parser.
	 */
	public MessageParser getMessageParser();
	/**
	 * Sets the message parser to use with this instance.
	 * 
	 * @param messageParser
	 *            The message parser.
	 */
	public void setMessageParser(MessageParser messageParser);
	/**
	 * Translates the message using the implementation-specific logic.
	 * 
	 * @param id
	 *            The message to translate.
	 * @param locale
	 *            The locale to use for translation.
	 * @return The translated string, which may contain placeholders.
	 */
	public String translate(Message id, Locale locale);
	/**
	 * Translates and fulfills placeholders on the provided message. Equivalent
	 * to {@code parse(id, data, getDefaultLocale())}.
	 * 
	 * @param id
	 *            The message to translate.
	 * @param data
	 *            The data for the placeholders, if any.
	 * @return The translated and parsed string.
	 */
	public default String parse(Message id, ArgMap data) {
		return parse(id, data, getDefaultLocale());
	}
	/**
	 * Translates and fulfills placeholders on the provided message, using the
	 * provided locale.
	 * 
	 * @param id
	 *            The message to translate.
	 * @param data
	 *            The data for the placeholders, if any.
	 * @param locale
	 *            The locale to use.
	 * @return The translated and parsed string.
	 */
	public default String parse(Message id, ArgMap data, Locale locale) {
		return parse(translate(id, locale), data, locale);
	}
	/**
	 * Fulfills placeholders on the provided text. Equivalent to
	 * {@code parse(msg, data, getDefaultLocale())}.
	 * 
	 * @param msg
	 *            The text to parse.
	 * @param data
	 *            The placeholder data.
	 * @return The parsed string.
	 */
	public default String parse(String msg, ArgMap data) {
		return parse(msg, data, getDefaultLocale());
	}
	/**
	 * Fulfills placeholders on the provided text, using the provided locale.
	 * 
	 * @param msg
	 *            The text to parse.
	 * @param data
	 *            The placeholder data.
	 * @param locale
	 *            The locale used in parsing, if needed.
	 * @return The parsed string.
	 */
	public default String parse(String msg, ArgMap data, Locale locale) {
		return getMessageParser().parse(msg, data, locale, this);
	}
}
