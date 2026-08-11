package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.BlendMethod;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BlendMethodAdapter implements JsonDeserializer<BlendMethod> {
    @Override
    public BlendMethod deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonElement.isJsonPrimitive()) return null;
        return switch (jsonElement.getAsJsonPrimitive().getAsString().toUpperCase()) {
            case "ADD" -> BlendMethod.ADD;
            case "SET" -> BlendMethod.SET;
            default -> null;
        };
    }
}
