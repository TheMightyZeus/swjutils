package com.seiferware.java.utils.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * An interface similar to {@link Map} that associates each value with two keys. A value stored with keys {@code k1} and
 * {@code k2} can only be retrieved with the same two keys, in the same order.
 *
 * @param <K1>
 * 		The type of the first key.
 * @param <K2>
 * 		The type of the second key.
 * @param <V>
 * 		The type of the value.
 */
public interface TwoKeyMap<K1, K2, V> {
	/**
	 * Removes all of the mappings from this map (optional operation). The map will be empty after this call returns.
	 */
	void clear();
	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified keys. More formally, returns <tt>true</tt>
	 * if and only if this map contains a mapping for keys <tt>k1</tt> and <tt>k2</tt> such that <tt>(key1==null ?
	 * k1==null : key1.equals(k1)) && (key2==null ? k2==null : key2.equals(k2))</tt> . (There can be at most one such
	 * mapping.)
	 *
	 * @param key1
	 * 		First key whose presence in this map is to be tested
	 * @param key2
	 * 		Second key whose presence in this map is to be tested
	 *
	 * @return <tt>true</tt> if this map contains a mapping for the specified keys
	 */
	boolean containsKeys(@NotNull K1 key1, @NotNull K2 key2);
	/**
	 * Returns <tt>true</tt> if this map maps one or more key pairs to the specified value. More formally, returns
	 * <tt>true</tt> if and only if this map contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>.
	 *
	 * @param value
	 * 		value whose presence in this map is to be tested
	 *
	 * @return <tt>true</tt> if this map maps one or more key pairs to the specified value
	 */
	boolean containsValue(@Nullable V value);
	/**
	 * Returns the value to which the specified keys are mapped, or {@code null} if this map contains no mapping for the
	 * key pair.
	 * <p>
	 * If this map permits null values, then a return value of {@code null} does not <i>necessarily</i> indicate that
	 * the map contains no mapping for the keys; it's also possible that the map explicitly maps the keys to {@code
	 * null}. The {@link #containsKeys containsKey} operation may be used to distinguish these two cases.
	 *
	 * @param key1
	 * 		The first key whose associated value is to be returned
	 * @param key2
	 * 		The second key whose associated value is to be returned
	 *
	 * @return the value to which the specified keys are mapped, or {@code null} if this map contains no mapping for the
	 * keys
	 */
	@Nullable V get(@NotNull K1 key1, @NotNull K2 key2);
	/**
	 * Returns <tt>true</tt> if this map contains no key-key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-key-value mappings
	 */
	boolean isEmpty();
	/**
	 * Associates the specified value with the specified key pair in this map (optional operation). If the map
	 * previously contained a mapping for the keys, the old value is replaced by the specified value. (A map <tt>m</tt>
	 * is said to contain a mapping for keys <tt>k1</tt>, <tt>k2</tt> if and only if {@link #containsKeys(Object,
	 * Object) m.containsKeys(k1, k2)} would return <tt>true</tt> .)
	 *
	 * @param key1
	 * 		First key with which the specified value is to be associated
	 * @param key2
	 * 		Second key with which the specified value is to be associated
	 * @param value
	 * 		value to be associated with the specified keys
	 *
	 * @return the previous value associated with <tt>key1</tt>, <tt>key2</tt>, or <tt>null</tt> if there was no such
	 * mapping. (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with the
	 * keys, if the implementation supports <tt>null</tt> values.)
	 */
	@Nullable V put(@NotNull K1 key1, @NotNull K2 key2, @Nullable V value);
	/**
	 * Removes the mapping for a key pair from this map if it is present (optional operation). <p> Returns the value to
	 * which this map previously associated the keys, or <tt>null</tt> if the map contained no mapping for the keys.
	 * </p> <p> If this map permits null values, then a return value of <tt>null</tt> does not <i>necessarily</i>
	 * indicate that the map contained no mapping for the keys; it's also possible that the map explicitly mapped the
	 * keys to <tt>null</tt>. </p> <p> The map will not contain a mapping for the specified key pair once the call
	 * returns. </p>
	 *
	 * @param key1
	 * 		First key whose mapping is to be removed from the map
	 * @param key2
	 * 		Second key whose mapping is to be removed from the map
	 *
	 * @return the previous value associated with the keys, or <tt>null</tt> if there was no such mapping.
	 */
	@Nullable V remove(@NotNull K1 key1, @NotNull K2 key2);
	/**
	 * Returns the number of key-key-value mappings in this map. If the map contains more than {@code Integer.MAX_VALUE}
	 * elements, returns {@code Integer.MAX_VALUE}.
	 *
	 * @return the number of key-key-value mappings in this map
	 */
	int size();
}
