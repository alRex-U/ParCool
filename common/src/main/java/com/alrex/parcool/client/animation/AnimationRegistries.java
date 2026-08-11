package com.alrex.parcool.client.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationRegistries {
    private static AnimationRegistries INSTANCE = null;

    public static void register() {
        if (INSTANCE == null) {
            INSTANCE = new AnimationRegistries();
        }
    }

    public static AnimationRegistries get() {
        return INSTANCE;
    }

    private final ParCoolAnimationProgresses progresses;
    private final ParCoolBlendingFactors blendingFactors;
    private final ParCoolCodedAnimationComponents components;
    private final ParCoolAnimations animations;

    public ParCoolAnimationProgresses progresses() {
        return progresses;
    }

    public ParCoolBlendingFactors blendingFactors() {
        return blendingFactors;
    }

    public ParCoolCodedAnimationComponents components() {
        return components;
    }

    public ParCoolAnimations animations() {
        return animations;
    }

    private AnimationRegistries() {
        progresses = new ParCoolAnimationProgresses();
        blendingFactors = new ParCoolBlendingFactors();
        components = new ParCoolCodedAnimationComponents();
        animations = new ParCoolAnimations();
    }
}
