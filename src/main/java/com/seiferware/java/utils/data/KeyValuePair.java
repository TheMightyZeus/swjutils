package com.seiferware.java.utils.data;

import org.jetbrains.annotations.Nullable;

/**
 * A simple class to hold two linked pieces of data.
 * 
 * @param <K>
 *            The key type.
 * @param <V>
 *            The value type.
 */
public class KeyValuePair<K, V> {
	protected K key;
	protected V value;
	
	/**
	 * Creates an empty instance.
	 */
	public KeyValuePair() {}
	/**
	 * Creates and populates an instance.
	 * 
	 * @param key
	 *            The key.
	 * @param value
	 *            The value.
	 */
	public KeyValuePair(@Nullable K key, @Nullable V value) {
		this.key = key;
		this.value = value;
	}
	/**
	 * Retrieves the key currently assigned to this instance.
	 * 
	 * @return The key.
	 */
	@Nullable
	public K getKey() {
		return key;
	}
	/**
	 * Assigns a new key to the instance.
	 * 
	 * @param key
	 *            The key.
	 */
	public void setKey(@Nullable K key) {
		this.key = key;
	}
	/**
	 * Retrieves the value currently assigned to this instance.
	 * 
	 * @return The value.
	 */
	@Nullable
	public V getValue() {
		return value;
	}
	/**
	 * Assigns a new value to the instance.
	 * 
	 * @param value
	 *            The value.
	 */
	public void setValue(@Nullable V value) {
		this.value = value;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof KeyValuePair) {
			boolean ksame = key == null ? ((KeyValuePair<?, ?>)obj).getKey() == null : key.equals(((KeyValuePair<?, ?>)obj).getKey());
			boolean vsame = value == null ? ((KeyValuePair<?, ?>)obj).getValue() == null : value.equals(((KeyValuePair<?, ?>)obj).getValue());
			return ksame && vsame;
		}
		return false;
	}
}
