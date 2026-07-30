package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimationPhase;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record AnimationSet(
        ResourceLocation name,
        @Nullable AnimationComponentGroup introAnimation,
        AnimationComponentGroup mainAnimation,
        @Nullable AnimationComponentGroup outroAnimation,
        int fadeInDuration,
        int fadeOutDuration,
        float blendFactorInFirstPersonView
) {
    @Nullable
    AnimationComponentGroup getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }
}
