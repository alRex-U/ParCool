package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.data.Transition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EaseTypeAdapter implements JsonDeserializer<Transition.Easing.Type> {
    @Override
    public Transition.Easing.Type deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!jsonElement.isJsonPrimitive()) return null;
        return switch (jsonElement.getAsJsonPrimitive().getAsString()) {
            case "AUTO", "EASE_IN_OUT" -> Transition.Easing.Type.EASE_IN_OUT;
            case "EASE_IN" -> Transition.Easing.Type.EASE_IN;
            case "EASE_OUT" -> Transition.Easing.Type.EASE_OUT;
            default -> null;
        };
    }
}
