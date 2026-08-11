package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.data.TimedValue;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TimedValueAdapter implements JsonDeserializer<TimedValue> {
    @Override
    public TimedValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonObject obj) {
            return new TimedValue(obj.get("t").getAsFloat(), obj.get("v").getAsFloat());
        }
        return null;
    }
}
