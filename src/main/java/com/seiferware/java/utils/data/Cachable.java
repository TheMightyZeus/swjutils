package com.seiferware.java.utils.data;

import com.seiferware.java.utils.data.store.Storable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p> Implementing classes can use this interface to store data that must be retrieved from an external source. </p>
 * <p> This is useful if retrieving the data is costly, and therefore caching the value and optionally deferring loading
 * is beneficial. </p> <p> Implementing classes may also choose to retrieve the value right away, but asynchronously. In
 * this case, the overhead of retrieving the value is taken away from the calling thread. </p> <p> This can also be
 * useful when the data is not guaranteed to be available when the key is set. In this case deferred loading is not only
 * helpful but necessary. </p> <p> If an implementing class does not support setting the value directly, a {@link
 * UnsupportedOperationException} may be thrown by {@link #setValue(Object) setValue}. </p>
 *
 * @param <K>
 * 		The type of index used to reference the value.
 * @param <V>
 * 		The type of the value.
 */
public interface Cachable<K, V> {
	/**
	 * Returns the key which is used to identify and retrieve the object to be cached.
	 *
	 * @return The key.
	 */
	@Nullable K getKey();
	/**
	 * Sets the key by which the cached object is identified. Implementing classes may use this key to retrieve the
	 * object immediately, asynchronously for example, or may choose to retrieve the value only when {@link #getValue()}
	 * is called.
	 *
	 * @param key
	 * 		The key.
	 */
	void setKey(@Nullable K key);
	/**
	 * It may be necessary at runtime to determine the type of key used by this instance. In particular, the
	 * functionality of {@link Storable} to seamlessly save a {@link Cachable} using the key without specifying getters
	 * and setters depends on this information. Therefore, this method allows that information to be retained even after
	 * type erasure.
	 *
	 * @return The {@link Class} representing the type used as the key.
	 */
	@NotNull Class<K> getKeyClass();
	/**
	 * Returns the value of the object from the caching mechanism if present, or retrieves it using whatever external
	 * method is used by the implementing class.
	 *
	 * @return The value.
	 */
	@Nullable V getValue();
	/**
	 * Sets the value directly. It is required that the implementing class be aware of how to retrieve the relevant key
	 * from the object, in order to keep the key and value in sync. Any classes that cannot accomplish this, or choose
	 * not to implement this method for any other reason, should throw an {@link UnsupportedOperationException}.
	 *
	 * @param value
	 * 		The value to set.
	 */
	void setValue(@Nullable V value);
	/**
	 * It may be necessary at runtime to determine the type of value used by this instance. Therefore, this method
	 * allows that information to be retained even after type erasure.
	 *
	 * @return The {@link Class} representing the type used as the value.
	 */
	@NotNull Class<V> getValueClass();
	/**
	 * <p> Marks the value as invalid. This <b>should</b> mean that the next time {@link #getValue()} is called, the
	 * value is retrieved again, or alternately that the value is immediately retrieved. </p> <p> Classes may choose not
	 * to implement this method, such as if the value associated with a given key is guaranteed never to change. In that
	 * case, they should throw an {@link UnsupportedOperationException}. </p>
	 */
	void invalidate();
	/**
	 * Indicates whether the cached value is valid. That is, the value has been loaded into the cache (or set directly)
	 * and not subsequently invalidated by {@link #invalidate()} or any other means.
	 *
	 * @return Whether the cache is valid.
	 */
	boolean isValid();
}
