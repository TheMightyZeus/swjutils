package com.seiferware.java.utils.collections;

import com.seiferware.java.utils.data.KeyValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link TwoKeyMap} that is backed by a single {@link HashMap HashMap&lt;KeyValuePair&lt;K1,
 * K2&gt;, V&gt;}.
 *
 * @param <K1>
 * 		The type of the first key.
 * @param <K2>
 * 		The type of the second key.
 * @param <V>
 * 		The type of the value.
 */
public class KVPTwoKeyMap<K1, K2, V> implements TwoKeyMap<K1, K2, V> {
	private final Map<KeyValuePair<K1, K2>, V> map = new HashMap<>();
	@Override
	public void clear() {
		map.clear();
	}
	@Override
	public boolean containsKeys(@NotNull K1 key1, @NotNull K2 key2) {
		return map.containsKey(new KeyValuePair<>(key1, key2));
	}
	@Override
	public boolean containsValue(@Nullable V value) {
		return map.containsValue(value);
	}
	@Nullable
	@Override
	public V get(@NotNull K1 key1, @NotNull K2 key2) {
		return map.get(new KeyValuePair<>(key1, key2));
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override
	public V put(@NotNull K1 key1, @NotNull K2 key2, @Nullable V value) {
		return map.put(new KeyValuePair<>(key1, key2), value);
	}
	@Override
	public V remove(@NotNull K1 key1, @NotNull K2 key2) {
		return map.remove(new KeyValuePair<>(key1, key2));
	}
	@Override
	public int size() {
		return map.size();
	}
}
