package com.alrex.parcool.client.animation.system;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
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
