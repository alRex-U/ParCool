package com.alrex.parcool.client.animation.system.config;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.ID;

public abstract class AnimationSystemConfig {
    private static AnimationSystemConfig instance;

    public static AnimationSystemConfig getInstance() {
        return instance;
    }

    public static void init(AnimationSystemConfig config) {
        if (instance == null) {
            instance = config;
        }
    }

    public abstract boolean isAvailable(ID<AnimationSet> id);

    public abstract boolean enableAnimation();

    public abstract boolean enableCameraAnimation();
}
