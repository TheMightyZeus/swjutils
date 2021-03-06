package com.seiferware.java.utils.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class that assumes some common operations of {@link Cachable}, leaving only the retrieval of the data by
 * key unimplemented for child classes. If asynchronous retrieval of values is required, use {@link
 * SimpleAsyncCachable}.
 *
 * @param <K>
 * 		The type of index used to reference the value.
 * @param <V>
 * 		The type of the value.
 *
 * @see Cachable
 * @see SimpleAsyncCachable
 */
public abstract class SimpleCachable<K, V> implements Cachable<K, V> {
	private final Class<K> keyClass;
	private final Class<V> valueClass;
	private boolean valid = false;
	private K key;
	private V value;
	/**
	 * This constructor is necessary for {@link #getKeyClass()} and {@link #getValueClass()} to work. Since retrieving
	 * a
	 * value by key is usually going to involve knowing the types ahead of time, implementing classes are not typically
	 * expected to be generic. Therefore it should typically be possible for child classes to pass these parameters in
	 * without receiving them in their own constructors.
	 *
	 * @param keyClass
	 * 		The type used for the key.
	 * @param valueClass
	 * 		The type used for the value.
	 */
	protected SimpleCachable(@NotNull Class<K> keyClass, @NotNull Class<V> valueClass) {
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}
	@Override
	@Nullable
	public K getKey() {
		V v = getValue();
		if(v != null) {
			try {
				return getKeyForValue(v);
			} catch (UnsupportedOperationException ignored) {
			}
		}
		return key;
	}
	@Override
	public void setKey(@Nullable K key) {
		this.key = key;
		invalidate();
	}
	@Override
	@NotNull
	public Class<K> getKeyClass() {
		return keyClass;
	}
	/**
	 * Optional. Child classes can override this method to retrieve keys from values. This allows values to be set
	 * directly. By default, {@link #setValue} will throw an {@link UnsupportedOperationException}.
	 *
	 * @param value
	 * 		The value used to retrieve the key.
	 *
	 * @return The key associated with the value, or {@code null} if {@code value} is {@code null}.
	 * @throws UnsupportedOperationException
	 * 		Unless overridden by a child class.
	 */
	@Nullable
	protected K getKeyForValue(V value) {
		throw new UnsupportedOperationException();
	}
	@Override
	@Nullable
	public V getValue() {
		if(valid) {
			return value;
		} else {
			value = retrieve(key);
			valid = true;
			return value;
		}
	}
	@Override
	public void setValue(@Nullable V value) {
		this.key = getKeyForValue(value);
		this.value = value;
		valid = true;
	}
	@Override
	@NotNull
	public Class<V> getValueClass() {
		return valueClass;
	}
	@Override
	public void invalidate() {
		valid = false;
	}
	@Override
	public boolean isValid() {
		return valid;
	}
	/**
	 * Retrieve the value associated with the key. This is the only method that must be implemented by a concrete child
	 * class.
	 *
	 * @param key
	 * 		The key used to retrieve the value.
	 *
	 * @return The value associated with the key. May return null if no such mapping exists.
	 */
	@Nullable
	protected abstract V retrieve(K key);
}
