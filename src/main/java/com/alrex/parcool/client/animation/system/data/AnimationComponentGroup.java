package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public record AnimationComponentGroup(List<ComponentEntry> components, int duration, boolean loops, boolean infinite) {
    public record ComponentEntry(
            IAnimationComponent component,
            @Nullable Supplier<IBlendingFactor> blendingFactor,
            Supplier<AnimationProgress> progressSupplier,
            boolean mirror
    ) {
    }
}
