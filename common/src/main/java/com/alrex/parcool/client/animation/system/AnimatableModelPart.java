package com.alrex.parcool.client.animation.system;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public enum AnimatableModelPart {
    BODY, HEAD, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG;
    private static final List<AnimatableModelPart> MIRROR;

    static {
        MIRROR = List.of(
                BODY, HEAD, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
        );
    }

    public AnimatableModelPart getMirrorPart() {
        return MIRROR.get(this.ordinal());
    }
}
