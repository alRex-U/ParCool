package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.resource.Argument;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class AnimationProgresses extends BasicRegistry<AnimationProgress, AnimationProgresses.RegistrationEntry<?>> {
    public record RegistrationEntry<T extends AnimationProgress>(
            ResourceLocation name,
            AnimationProgress.Constructor<T> constructor
    ) {
    }

    private AnimationProgresses() {
    }

    @Nullable
    private static AnimationProgresses INSTANCE = null;

    public static AnimationProgresses getInstance() {
        if (INSTANCE == null) INSTANCE = new AnimationProgresses();
        return INSTANCE;
    }

    public ID<AnimationProgress> register(String subName, AnimationProgress.IDeltaProgressProvider progressProvider) {
        return this.register(subName, (loop, min, max, args) -> new AnimationProgress.FunctionDeltaAnimationProgress(loop, min, max, args, progressProvider));
    }

    public ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.IDeltaProgressProvider progressProvider) {
        return register(name, (loop, min, max, args) -> new AnimationProgress.FunctionDeltaAnimationProgress(loop, min, max, args, progressProvider));
    }

    public ID<AnimationProgress> register(String subName, AnimationProgress.IDirectProgressProvider progressProvider) {
        return this.register(subName, (loop, min, max, args) -> new AnimationProgress.FunctionDirectAnimationProgress(args, progressProvider));
    }

    public ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.IDirectProgressProvider progressProvider) {
        return this.register(name, (loop, min, max, args) -> new AnimationProgress.FunctionDirectAnimationProgress(args, progressProvider));
    }

    public ID<AnimationProgress> register(String subName, AnimationProgress.Constructor<?> progressProvider) {
        return registerItem(subName, new RegistrationEntry<>(ParCool.resourceLocation(subName), progressProvider));
    }

    public ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.Constructor<?> progressProvider) {
        return registerItem(name, new RegistrationEntry<>(name, progressProvider));
    }

    public AnimationProgress getNewInstance(ID<AnimationProgress> id, boolean loop, float rangeMin, float rangeMax, Argument argument) {
        return getRegistry().get(id).constructor.newInstance(loop, rangeMin, rangeMax, argument);
    }

    public AnimationProgress getNewInstance(ID<AnimationProgress> id) {
        return getRegistry().get(id).constructor.newInstance(false, 0f, Float.MAX_VALUE, Argument.EMPTY);
    }

    public final ID<AnimationProgress> TIME = register("time", (player) -> 1);
    public final ID<AnimationProgress> VELOCITY = register("velocity", (player) -> (float) EntityUtil.getPositionDifference(player).length());
    public final ID<AnimationProgress> VELOCITY_H = register("velocity_h", (player) -> (float) Math.min(EntityUtil.getHorizontalPositionDifference(player).length(), EntityUtil.getHorizontalMaximumSpeed(player)));
    public final ID<AnimationProgress> VELOCITY_V = register("velocity_v", (player) -> (float) Math.abs(player.position().y - player.yo));
    public final ID<AnimationProgress> VELOCITY_FORWARD = register("velocity_forward", (player) -> (float) Math.min(EntityUtil.getHorizontalPositionDifference(player).dot(EntityUtil.getHorizontalLookAngle(player)), EntityUtil.getHorizontalMaximumSpeed(player)));
    public final ID<AnimationProgress> VELOCITY_LEFT = register("velocity_left", (player) -> (float) Math.min(EntityUtil.getHorizontalPositionDifference(player).dot(EntityUtil.getHorizontalLookAngle(player).yRot(Mth.HALF_PI)), EntityUtil.getHorizontalMaximumSpeed(player)));
    public final ID<AnimationProgress> VELOCITY_UP = register("velocity_up", (player) -> (float) (player.position().y - player.yo));
}
