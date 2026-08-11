package com.alrex.parcool.common;

import java.util.Map;
import java.util.TreeMap;

public class BasicRegistry<KEY extends Comparable<KEY>, VALUE> {
    private final TreeMap<KEY, VALUE> registry = new TreeMap<>();
    private boolean frozen;

    protected Map<KEY, VALUE> getRegistry() {
        return registry;
    }

    protected void register(KEY key, VALUE value) {
        if (frozen) {
            throw new IllegalStateException("This registry is already frozen");
        }
        if (registry.containsKey(key)) {
            throw new IllegalStateException(String.format("Key[%s] is already registered", key));
        }
        registry.put(key, value);
    }

    public void freeze() {
        this.frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }
}
