package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * An implementation of {@link MessageSource} that is backed by one or more {@link ResourceBundle}s.
 */
public class ResourceBundleMessageSource implements MessageSource {
	protected Locale defaultLocale = Locale.getDefault();
	protected MessageParser messageParser = MessageParser.getDefault();
	protected String bundleName;
	protected Map<String, ResourceBundle> bundles = new HashMap<>();
	protected Map<String, String> extraBundles = new HashMap<>();
	protected ResourceBundle.Control control = new PropertiesFileResourceBundleControl();
	protected ResourceBundleProvider provider;
	/**
	 * Generates the message source using the provided base bundle name.
	 *
	 * @param bundleName
	 * 		The base name of the default bundle.
	 */
	@NotNull
	public ResourceBundleMessageSource(@NotNull String bundleName) {
		this.bundleName = bundleName;
		getBundle(defaultLocale);
	}
	/**
	 * <p> Adds an additional bundle to the message source. To reference secondary bundles, use {@code shortcut},
	 * followed by a colon, followed by the resource name as the return value of {@link Message#getValue()}. </p> <p>
	 * For example, {@code myBundle:myMessage} will fetch the translated version of the {@code myMessage} resource from
	 * a bundle that has been assigned the {@code shortcut} of {@code myBundle}. </p>
	 *
	 * @param shortcut
	 * 		The name by which the bundle will be referenced in messages.
	 * @param bundleName
	 * 		The base resource bundle name.
	 */
	public void addBundle(@NotNull String shortcut, @NotNull String bundleName) {
		extraBundles.put(shortcut, bundleName);
	}
	@NotNull
	protected ResourceBundle getBundle(@NotNull Locale locale) {
		String lang = locale.getLanguage();
		if(!bundles.containsKey(lang)) {
			bundles.put(lang, ResourceBundle.getBundle(bundleName, locale, control));
		}
		return bundles.get(lang);
	}
	@NotNull
	protected ResourceBundle getBundle(@NotNull String shortcut, @NotNull Locale locale) {
		String lang = shortcut + ":" + locale.getLanguage();
		if(!bundles.containsKey(lang)) {
			ResourceBundle rb = null;
			if(extraBundles.containsKey(shortcut)) {
				rb = ResourceBundle.getBundle(extraBundles.get(shortcut), locale, control);
			} else if(provider != null) {
				rb = provider.getBundle(shortcut, locale);
			}
			if(rb != null) {
				bundles.put(lang, rb);
				return rb;
			} else {
				return getBundle(locale);
			}
		}
		return bundles.get(lang);
	}
	/**
	 * Gets the base name of the default resource bundle for this instance.
	 *
	 * @return The bundle name.
	 */
	@NotNull
	public String getBundleName() {
		return bundleName;
	}
	/**
	 * Sets the base name of the default resource bundle for this instance.
	 *
	 * @param bundleName
	 * 		The bundle name.
	 */
	public void setBundleName(@NotNull String bundleName) {
		this.bundleName = bundleName;
		bundles = new HashMap<>();
	}
	@Override
	public @NotNull Locale getDefaultLocale() {
		return defaultLocale;
	}
	@Override
	public void setDefaultLocale(@NotNull Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
		getBundle(defaultLocale);
	}
	@NotNull
	@Override
	public MessageParser getMessageParser() {
		return messageParser;
	}
	@Override
	public void setMessageParser(@NotNull MessageParser messageParser) {
		this.messageParser = messageParser;
	}
	/**
	 * Causes the message parser to reload all language versions of all loaded bundles.
	 */
	public void invalidateAllBundles() {
		bundles.clear();
		ResourceBundle.clearCache();
	}
	/**
	 * Causes the message parser to reload the specified language version of the specified bundle, or the default
	 * bundle if {@code shortcut} is {@code null}.
	 *
	 * @param shortcut
	 * 		The bundle to be reloaded, or {@code null} to reload the default bundle.
	 * @param locale
	 * 		The language of the bundle to reload.
	 */
	public void invalidateBundle(@Nullable String shortcut, @NotNull Locale locale) {
		String lang = shortcut == null ? locale.getLanguage() : shortcut + ":" + locale.getLanguage();
		if(bundles.containsKey(lang)) {
			bundles.remove(lang);
		}
		ResourceBundle.clearCache();
	}
	/**
	 * Causes the message parser to reload all language versions of the specified bundle, or the default bundle if
	 * {@code shortcut} is {@code null}.
	 *
	 * @param shortcut
	 * 		The bundle to be reloaded, or {@code null} to reload the default bundle.
	 */
	public void invalidateBundleSet(@Nullable String shortcut) {
		for(String s : new HashSet<>(bundles.keySet())) {
			if(shortcut == null && !s.contains(":")) {
				bundles.remove(s);
			} else if(shortcut != null && s.startsWith(shortcut + ":")) {
				bundles.remove(s);
			}
		}
		ResourceBundle.clearCache();
	}
	/**
	 * Set or remove the {@link ResourceBundleProvider} associated with this instance. This message source will attempt
	 * to load resource bundles from this provider if they are not already registered.
	 *
	 * @param provider
	 * 		The instance that will provide resource bundles.
	 */
	public void setBundleProvider(@Nullable ResourceBundleProvider provider) {
		this.provider = provider;
	}
	@NotNull
	@Override
	public String translate(@NotNull Message id, @NotNull Locale locale) throws TranslationException {
		String msgid = id.getValue();
		int sep = msgid.indexOf(':');
		if(sep >= 0) {
			String bundle = msgid.substring(0, sep);
			String key = msgid.substring(sep + 1);
			try {
				return getBundle(bundle, locale).getString(key);
			} catch (MissingResourceException e) {
				throw new TranslationException("Missing Resource: " + msgid, e);
			}
		}
		try {
			return getBundle(locale).getString(msgid);
		} catch (MissingResourceException e) {
			throw new TranslationException("Missing Resource: " + msgid, e);
		}
	}
}
