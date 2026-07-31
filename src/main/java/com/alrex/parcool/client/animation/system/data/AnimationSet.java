package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimationPhase;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record AnimationSet(
        ResourceLocation name,
        @Nullable AnimationComponentGroup introAnimation,
        AnimationComponentGroup mainAnimation,
        @Nullable AnimationComponentGroup outroAnimation,
        @Nullable Supplier<IBlendingFactor> blendingFactorSupplier,
        int fadeInDuration,
        int fadeOutDuration,
        float blendFactorInFirstPersonView,
        @Nullable
        Vec3f cameraAnimationScales
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
