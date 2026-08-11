package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.resource.Argument;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.TreeMap;

public class ArgumentAdapter implements JsonDeserializer<Argument> {
    @Override
    public Argument deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonObject object)) return Argument.EMPTY;
        var map = new TreeMap<String, Object>();
        for (var elem : object.entrySet()) {
            if (elem.getValue() instanceof JsonPrimitive primitive) {
                if (primitive.isString()) {
                    map.put(elem.getKey(), primitive.getAsString());
                    continue;
                }
                if (primitive.isNumber()) {
                    map.put(elem.getKey(), primitive.getAsFloat());
                    continue;
                }
                if (primitive.isBoolean()) {
                    map.put(elem.getKey(), primitive.getAsBoolean());
                    continue;
                }
            }
        }
        return new Argument(map);
    }
}
