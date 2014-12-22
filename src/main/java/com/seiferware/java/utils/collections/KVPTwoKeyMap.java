package com.seiferware.java.utils.collections;

import java.util.HashMap;
import java.util.Map;

import com.seiferware.java.utils.data.KeyValuePair;

/**
 * An implementation of {@link TwoKeyMap} that is backed by a single
 * {@link HashMap HashMap&lt;KeyValuePair&lt;K1, K2&gt;, V&gt;}.
 * 
 * @param <K1>
 *            The type of the first key.
 * @param <K2>
 *            The type of the second key.
 * @param <V>
 *            The type of the value.
 */
public class KVPTwoKeyMap<K1, K2, V> implements TwoKeyMap<K1, K2, V> {
	private Map<KeyValuePair<K1, K2>, V> map = new HashMap<>();
	@Override
	public int size() {
		return map.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override
	public boolean containsKeys(K1 key1, K2 key2) {
		return map.containsKey(new KeyValuePair<>(key1, key2));
	}
	@Override
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}
	@Override
	public V get(K1 key1, K2 key2) {
		return map.get(new KeyValuePair<>(key1, key2));
	}
	@Override
	public V put(K1 key1, K2 key2, V value) {
		return map.put(new KeyValuePair<>(key1, key2), value);
	}
	@Override
	public V remove(K1 key1, K2 key2) {
		return map.remove(new KeyValuePair<>(key1, key2));
	}
	@Override
	public void clear() {
		map.clear();
	}
}
