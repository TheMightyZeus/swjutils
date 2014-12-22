package com.seiferware.java.utils.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * An implementation of {@link MessageSource} that is backed by one or more
 * {@link ResourceBundle}s.
 */
public class ResourceBundleMessageSource implements MessageSource {
	protected Locale defaultLocale = Locale.getDefault();
	protected MessageParser messageParser = MessageParser.getDefault();
	protected String bundleName;
	protected Map<String, ResourceBundle> bundles = new HashMap<>();
	protected Map<String, String> extraBundles = new HashMap<>();
	
	/**
	 * Generates the message source using the provided base bundle name.
	 * 
	 * @param bundleName
	 *            The base name of the default bundle.
	 */
	public ResourceBundleMessageSource(String bundleName) {
		this.bundleName = bundleName;
		getBundle(defaultLocale);
	}
	protected ResourceBundle getBundle(String shortcut, Locale locale) {
		String lang = shortcut + ":" + locale.getLanguage();
		if(!bundles.containsKey(lang)) {
			if(extraBundles.containsKey(shortcut)) {
				bundles.put(lang, ResourceBundle.getBundle(extraBundles.get(shortcut), locale));
			} else {
				return getBundle(locale);
			}
		}
		return bundles.get(lang);
	}
	protected ResourceBundle getBundle(Locale locale) {
		String lang = locale.getLanguage();
		if(!bundles.containsKey(lang)) {
			bundles.put(lang, ResourceBundle.getBundle(bundleName, locale));
		}
		return bundles.get(lang);
	}
	/**
	 * <p>
	 * Adds an additional bundle to the message source. To reference secondary
	 * bundles, use {@code shortcut}, followed by a colon, followed by the
	 * resource name as the return value of {@link Message#getValue()}.
	 * </p>
	 * <p>
	 * For example, {@code myBundle:myMessage} will fetch the translated version
	 * of the {@code myMessage} resource from a bundle that has been assigned
	 * the {@code shortcut} of {@code myBundle}.
	 * </p>
	 * 
	 * @param shortcut
	 *            The name by which the bundle will be referenced in messages.
	 * @param bundleName
	 *            The base resource bundle name.
	 */
	public void addBundle(String shortcut, String bundleName) {
		extraBundles.put(shortcut, bundleName);
	}
	@Override
	public Locale getDefaultLocale() {
		return defaultLocale;
	}
	@Override
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
		getBundle(defaultLocale);
	}
	@Override
	public MessageParser getMessageParser() {
		return messageParser;
	}
	@Override
	public void setMessageParser(MessageParser messageParser) {
		this.messageParser = messageParser;
	}
	/**
	 * Gets the base name of the default resource bundle for this instance.
	 * 
	 * @return The bundle name.
	 */
	public String getBundleName() {
		return bundleName;
	}
	/**
	 * Sets the base name of the default resource bundle for this instance.
	 * 
	 * @param bundleName
	 *            The bundle name.
	 */
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
		bundles = new HashMap<>();
	}
	@Override
	public String translate(Message id, Locale locale) {
		String msgid = id.getValue();
		String msgval = "";
		int sep = msgid.indexOf(':');
		if(sep >= 0) {
			String bundle = msgid.substring(0, sep);
			String key = msgid.substring(sep + 1);
			msgval = getBundle(bundle, locale).getString(key);
		} else {
			msgval = getBundle(locale).getString(msgid);
		}
		return msgval;
	}
}
