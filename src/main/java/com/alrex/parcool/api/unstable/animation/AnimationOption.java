package com.alrex.parcool.api.unstable.animation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
public class AnimationOption {
    private final EnumMap<AnimationPart, Boolean> animationMap = new EnumMap<>(AnimationPart.class);
    private boolean animationCanceled = false;

    public AnimationOption() {
        for (AnimationPart part : AnimationPart.values()) {
            animationMap.put(part, true);
        }
    }

    public void cancel(AnimationPart part) {
        animationMap.put(part, false);
    }

    public void cancelAnimation() {
        for (AnimationPart part : AnimationPart.values()) {
            animationMap.put(part, false);
        }
        animationCanceled = true;
    }

    public boolean isCanceled(AnimationPart part) {
        if (animationCanceled) return true;
        Boolean value = animationMap.get(part);
        return Boolean.FALSE.equals(value);
    }

    public boolean isAnimationCanceled() {
        return animationCanceled;
    }
}
