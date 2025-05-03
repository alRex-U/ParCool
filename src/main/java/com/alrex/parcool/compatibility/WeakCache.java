package com.alrex.parcool.compatibility;

import java.util.Map;
import java.util.WeakHashMap;

import com.google.common.base.Supplier;

public class WeakCache<K, V> {
    private final Map<Class<?>, WeakHashMap<K, V>> cache = new WeakHashMap<>();

    public V get(K key, Supplier<V> create) {
        WeakHashMap<K,V> hashMap = cache.get(key.getClass());
        if (hashMap == null) {
            hashMap = new WeakHashMap<>();
            cache.put(key.getClass(), hashMap);
        }
        V value = hashMap.get(key);
        if (value != null) return value;
        value = create.get();
        hashMap.put(key, value);
        return value;
    }
}
