package com.alrex.parcool.client.animation.system.config;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.TreeMap;

public class AnimationSystemConfig {
    private final ForgeConfigSpec builtConfig;
    private final TreeMap<ID<AnimationSet>, ForgeConfigSpec.BooleanValue> animationAvailabilities;

    public AnimationSystemConfig(AnimationSets animations) {
        var builder = new ForgeConfigSpec.Builder();
        if (!animations.isFrozen()) {
            throw new IllegalStateException("It's impossible to create config for unfrozen AnimationSets");
        }
        builder.push("Animation");
        {
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

    public ForgeConfigSpec getBuiltConfig() {
        return builtConfig;
    }
}
