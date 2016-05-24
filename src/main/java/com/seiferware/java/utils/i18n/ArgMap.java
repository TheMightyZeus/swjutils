package com.seiferware.java.utils.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used for i18n to tie argument names to their associated objects. Used much like a {@link Map Map&lt;String,
 * Object&gt;}
 */
public class ArgMap {
	private static final ArgMap empty = new ArgMap(Collections.emptyMap());
	private Map<String, Object> inner;
	private ArgMap() {
		inner = new HashMap<>();
	}
	private ArgMap(@NotNull Map<String, Object> inner) {
		this.inner = inner;
	}
	/**
	 * Creates a new instance
	 *
	 * @return The created {@link ArgMap} instance.
	 */
	@NotNull
	public static ArgMap create() {
		return new ArgMap();
	}
	/**
	 * Creates a new instance and adds the specified argument. Equivalent to {@code ArgMap.create().put(s1, o1)}.
	 *
	 * @param s1
	 * 		The argument name
	 * @param o1
	 * 		The argument value
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public static ArgMap create(@NotNull String s1, @Nullable Object o1) {
		ArgMap result = create();
		result.put(s1, o1);
		return result;
	}
	/**
	 * Creates a new instance and adds the specified arguments. Equivalent to {@code ArgMap.create().put(s1,
	 * o1).put(s2,
	 * o2)}.
	 *
	 * @param s1
	 * 		The first argument name
	 * @param o1
	 * 		The first argument value
	 * @param s2
	 * 		The second argument name
	 * @param o2
	 * 		The second argument value
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public static ArgMap create(@NotNull String s1, @Nullable Object o1, @NotNull String s2, @Nullable Object o2) {
		return create().put(s1, o1).put(s2, o2);
	}
	/**
	 * Creates a new instance and adds the specified arguments. Equivalent to {@code ArgMap.create().put(s1,
	 * o1).put(s2,
	 * o2).put(s3, o3)}.
	 *
	 * @param s1
	 * 		The first argument name
	 * @param o1
	 * 		The first argument value
	 * @param s2
	 * 		The second argument name
	 * @param o2
	 * 		The second argument value
	 * @param s3
	 * 		The third argument name
	 * @param o3
	 * 		The third argument value
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public static ArgMap create(@NotNull String s1, @Nullable Object o1, @NotNull String s2, @Nullable Object o2, @NotNull String s3, @Nullable Object o3) {
		return create().put(s1, o1).put(s2, o2).put(s3, o3);
	}
	/**
	 * Empties this instance of all arguments.
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public static ArgMap empty() {
		return empty;
	}
	/**
	 * Removes all of the mappings from this instance. The instance will be empty after this call returns.
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public ArgMap clear() {
		inner.clear();
		return this;
	}
	/**
	 * Returns {@code true} if this instance contains a mapping for the specified key. More formally, returns {@code
	 * true} if and only if this map contains a mapping for a key {@code k} such that {@code (key==null ? k==null :
	 * key.equals(k))}. (There can be at most one such mapping.)
	 *
	 * @param key
	 * 		Key whose presence in this instance is to be tested
	 *
	 * @return {@code true} if this map contains a mapping for the specified key
	 */
	public boolean containsKey(@NotNull String key) {
		return inner.containsKey(key);
	}
	/**
	 * Creates a duplicate {@link ArgMap} from the current one.
	 *
	 * @return The duplicate ArgMap.
	 */
	public ArgMap copy() {
		Map<String, Object> a = new HashMap<>();
		a.putAll(inner);
		return new ArgMap(a);
	}
	/**
	 * <p> Returns the value to which the specified key is mapped, or {@code null} if this instance contains no mapping
	 * for the key. </p> <p> More formally, if this instance contains a mapping from a key {@code k} to a value {@code
	 * v} such that {@code (key==null ? k==null : key.equals(k))}, then this method returns {@code v}; otherwise it
	 * returns {@code null}. (There can be at most one such mapping.) </p> <p> Since null values are allowed, a return
	 * value of {@code null} does not <i>necessarily</i> indicate that the instance contains no mapping for the key;
	 * it's also possible that the instance explicitly maps the key to {@code null}. The {@link #containsKey} operation
	 * may be used to distinguish these two cases. </p>
	 *
	 * @param key
	 * 		The key whose associated value is to be returned
	 *
	 * @return The value to which the specified key is mapped, or {@code null} if this instance contains no mapping for
	 * the key
	 */
	@Nullable
	public Object get(@NotNull String key) {
		return inner.get(key);
	}
	/**
	 * Returns a {@link Set} view of the keys contained in this map.
	 *
	 * @return A set view of the keys contained in this map
	 */
	public Set<String> getKeys() {
		return Collections.unmodifiableSet(inner.keySet());
	}
	/**
	 * Returns an unmodifiable view of the instance. This method allows modules to provide users with "read-only"
	 * access
	 * to internal maps. Query operations on the returned map "read through" to the specified map, and attempts to
	 * modify the returned map result in an {@link UnsupportedOperationException}.
	 *
	 * @return An unmodifiable view of the specified map.
	 */
	@NotNull
	public ArgMap getUnmodifiable() {
		return new ArgMap(Collections.unmodifiableMap(inner));
	}
	/**
	 * Indicates whether this instance has any stored arguments.
	 *
	 * @return {@code true} if the instance has no elements, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return inner.isEmpty();
	}
	/**
	 * Associates the specified value with the specified key in this instance. If the instance previously contained a
	 * mapping for the key, the old value is replaced by the specified value. (An {@link ArgMap} {@code m} is said to
	 * contain a mapping for a key {@code k} if and only if {@link #containsKey(String) m.containsKey(k)} would return
	 * {@code true} .)
	 *
	 * @param key
	 * 		key with which the specified value is to be associated
	 * @param value
	 * 		value to be associated with the specified key
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public ArgMap put(@NotNull String key, @Nullable Object value) {
		inner.put(key, value);
		return this;
	}
	/**
	 * Removes the mapping for a key from this instance if it is present.
	 *
	 * @param key
	 * 		Key whose mapping is to be removed from the map.
	 *
	 * @return This {@link ArgMap} instance, for method chaining.
	 */
	@NotNull
	public ArgMap remove(@NotNull String key) {
		inner.remove(key);
		return this;
	}
	/**
	 * @return The number of elements currently stored in this instance.
	 */
	public int size() {
		return inner.size();
	}
}
