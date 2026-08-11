package com.alrex.parcool.forge.client.animation.system.config;

import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.TreeMap;

public class ForgeAnimationSystemConfig extends AnimationSystemConfig {

    private final ForgeConfigSpec builtConfig;
    private final TreeMap<ID<AnimationSet>, ForgeConfigSpec.BooleanValue> animationAvailabilities;
    private final ForgeConfigSpec.BooleanValue enableAnimation;
    private final ForgeConfigSpec.BooleanValue enableCameraAnimation;

    public ForgeAnimationSystemConfig(AnimationSets animations) {
        var builder = new ForgeConfigSpec.Builder();
        if (!animations.isFrozen()) {
            throw new IllegalStateException("It's impossible to create config for unfrozen AnimationSets");
        }
        builder.push("Animation");
        {
            enableAnimation = builder.define("enable", true);
            enableCameraAnimation = builder.define("enable_camera_animation", true);
            builder.push("Availability");
            {
                animationAvailabilities = new TreeMap<>();
                for (var animation : animations.getRegistry().entrySet()) {
                    animationAvailabilities.put(animation.getKey(),
                            builder.define(animation.getValue().location().toString(), true)
                    );
                }
            }
            builder.pop();
        }
        builder.pop();

        builtConfig = builder.build();
    }

    public boolean isAvailable(ID<AnimationSet> id) {
        var config = animationAvailabilities.get(id);
        if (config == null) return false;
        return config.get();
    }

    @Override
    public boolean enableAnimation() {
        return enableAnimation.get();
    }

    @Override
    public boolean enableCameraAnimation() {
        return enableCameraAnimation.get();
    }

    public ForgeConfigSpec getBuiltConfig() {
        return builtConfig;
    }
}
