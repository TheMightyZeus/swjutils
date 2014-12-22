package com.seiferware.java.utils.data;

/**
 * An abstract class that assumes some common operations of {@link Cachable} and
 * asynchronously polls the concrete implementation's method to retrieve the
 * value as soon as the key is set. If values are available right away, and
 * asynchronous loading is not required, use {@link SimpleCachable}.
 *
 * @param <K>
 *            The type of index used to reference the value.
 * @param <V>
 *            The type of the value.
 * @see Cachable
 * @see SimpleCachable
 */
public abstract class SimpleAsyncCachable<K, V> implements Cachable<K, V> {
	private boolean valid = false;
	private K key;
	private V value;
	private Class<K> keyClass;
	private Class<V> valueClass;
	
	/**
	 * This constructor is necessary for {@link #getKeyClass()} and
	 * {@link #getValueClass()} to work. Since retrieving a value by key is
	 * usually going to involve knowing the types ahead of time, implementing
	 * classes are not typically expected to be generic. Therefore it should
	 * typically be possible for child classes to pass these parameters in
	 * without receiving them in their own constructors.
	 * 
	 * @param keyClass
	 *            The type used for the key.
	 * @param valueClass
	 *            The type used for the value.
	 */
	protected SimpleAsyncCachable(Class<K> keyClass, Class<V> valueClass) {
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}
	/**
	 * Retrieve the value associated with the key. This is the only method that
	 * must be implemented by a concrete child class.
	 * 
	 * @param key
	 *            The key used to retrieve the value.
	 * @return The value associated with the key. May return null if no such
	 *         mapping exists.
	 */
	protected abstract V retrieve(K key);
	/**
	 * Optional. Child classes can override this method to retrieve keys from
	 * values. This allows values to be set directly. By default,
	 * {@link #setValue} will throw an {@link UnsupportedOperationException}.
	 * 
	 * @param value
	 *            The value used to retrieve the key.
	 * @return The key associated with the value, or {@code null} if
	 *         {@code value} is {@code null}.
	 * @throws UnsupportedOperationException
	 *             Unless overridden by a child class.
	 */
	protected K getKeyForValue(V value) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isValid() {
		return valid;
	}
	@Override
	public void invalidate() {
		valid = false;
		synchronized(this) {
			if(!valid) {
				new Thread(() -> {
					synchronized(this) {
						value = retrieve(key);
						valid = true;
					}
				}).run();
			}
		}
	}
	@Override
	public V getValue() {
		if(valid) {
			return value;
		} else {
			synchronized(this) {
				if(valid) {
					return value;
				}
				value = retrieve(key);
				valid = true;
				return value;
			}
		}
	}
	@Override
	public void setValue(V value) {
		this.key = getKeyForValue(value);
		this.value = value;
		valid = true;
	}
	@Override
	public K getKey() {
		return key;
	}
	@Override
	public void setKey(K key) {
		this.key = key;
		invalidate();
	}
	@Override
	public Class<K> getKeyClass() {
		return keyClass;
	}
	@Override
	public Class<V> getValueClass() {
		return valueClass;
	}
}
