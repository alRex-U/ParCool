package com.alrex.parcool.client.animation.system;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public enum AnimatableProperty {
    TRANSLATE_X(0),
    TRANSLATE_Y(0),
    TRANSLATE_Z(0),
    ROTATION_W(1),
    ROTATION_X(0),
    ROTATION_Y(0),
    ROTATION_Z(0);

    public static final List<AnimatableProperty> TRANSLATIONS = List.of(TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z);
    public static final List<AnimatableProperty> ROTATIONS = List.of(ROTATION_W, ROTATION_X, ROTATION_Y, ROTATION_Z);
    private final float defaultValue;

    AnimatableProperty(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }
}
