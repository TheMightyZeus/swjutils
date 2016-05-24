package com.seiferware.java.utils.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

/**
 * Provides a wrapper around a {@link Properties} object that provides convenience methods for interpreting various
 * types of data.
 */
public class PropertyReader {
	private final Properties props;
	/**
	 * Creates the property reader, binding it to a properties object.
	 *
	 * @param properties
	 * 		The object to read the values from.
	 */
	public PropertyReader(final @NotNull Properties properties) {
		props = properties;
	}
	/**
	 * Retrieves a property value from the object, and returns it as a boolean.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object, or the value cannot
	 * 		be parsed as a boolean.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present or the value
	 * is not valid.
	 */
	public boolean getBool(@NotNull String key, boolean defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(props.getProperty(key));
	}
	/**
	 * Retrieves a property value from the object, and returns it as a double.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object, or the value cannot
	 * 		be parsed as a double.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present or the value
	 * is not valid.
	 */
	public double getDouble(@NotNull String key, double defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(props.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	/**
	 * Retrieves a property value from the object, and returns it as a float.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object, or the value cannot
	 * 		be parsed as a float.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present or the value
	 * is not valid.
	 */
	public float getFloat(@NotNull String key, float defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(props.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	/**
	 * Retrieves a property value from the object, and returns it as an integer.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object, or the value cannot
	 * 		be parsed as an integer.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present or the value
	 * is not valid.
	 */
	public int getInt(@NotNull String key, int defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(props.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	/**
	 * Retrieves a property value from the object, and returns it as a long.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object, or the value cannot
	 * 		be parsed as a long.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present or the value
	 * is not valid.
	 */
	public long getLong(@NotNull String key, long defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(props.getProperty(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	/**
	 * Retrieves a property value from the object, and returns it as a string.
	 *
	 * @param key
	 * 		The key that refers to the desired value.
	 * @param defaultValue
	 * 		The default value to use if {@code key} does not exist as a key in the properties object.
	 *
	 * @return The value associated with {@code key}, or {@code defaultValue} if {@code key} is not present.
	 */
	@Nullable
	@Contract("_, !null -> !null")
	public String getString(@NotNull String key, @Nullable String defaultValue) {
		if(!props.containsKey(key)) {
			return defaultValue;
		}
		return props.getProperty(key);
	}
}
