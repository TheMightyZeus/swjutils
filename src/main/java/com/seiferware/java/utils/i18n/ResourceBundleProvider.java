package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is an optional component to attach to a {@link ResourceBundleMessageSource} that allows it to load {@link
 * ResourceBundle}s on demand. When a message is passed to the message source with an unknown shortcut, the message
 * source attempts to use this interface to load an appropriate one.
 *
 * @see ResourceBundleMessageSource
 */
@FunctionalInterface
public interface ResourceBundleProvider {
	/**
	 * Attempt to load a resource bundle based on the provided shortcut.
	 *
	 * @param shortcut
	 * 		The shortcut used to refer to the resource bundle.
	 * @param locale
	 * 		The locale of the bundle to load.
	 *
	 * @return The resource bundle matching the shortcut, or {@code null} if it cannot be found.
	 */
	@Nullable ResourceBundle getBundle(@NotNull String shortcut, @NotNull Locale locale);
}
