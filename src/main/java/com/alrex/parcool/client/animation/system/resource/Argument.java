package com.alrex.parcool.client.animation.system.resource;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Argument {
    public static final Argument EMPTY = new Argument(Collections.emptyMap());

    private final Map<String, Object> argMap;

    public Argument(Map<String, Object> argMap) {
        this.argMap = argMap;
    }

    public String request(String key, String defaultValue) {
        if (argMap.get(key) instanceof String data) {
            return data;
        }
        return defaultValue;
    }

    public float request(String key, float defaultValue) {
        if (argMap.get(key) instanceof Float data) {
            return data;
        }
        return defaultValue;
    }

    public boolean request(String key, boolean defaultValue) {
        if (argMap.get(key) instanceof Boolean data) {
            return data;
        }
        return defaultValue;
    }
}
