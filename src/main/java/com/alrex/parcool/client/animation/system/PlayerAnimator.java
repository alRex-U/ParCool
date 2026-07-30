package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class PlayerAnimator {
    public static PlayerAnimator get(AbstractClientPlayer player) {
        return ((IPlayerAnimatorHolder) player).getParCoolPlayerAnimator();
    }

    private final AnimationProcessor animationProcessor;
    @Nullable
    private BlendingModelTransform currentTransformation = null;

    public PlayerAnimator(AbstractClientPlayer owner) {
        animationProcessor = new AnimationProcessor(owner);
    }

    public void tick() {
        animationProcessor.tick();
    }

    public void onRenderTick(AbstractClientPlayer player, float partialTick) {
        updateTransformation(player, Minecraft.getInstance().options.getCameraType().isFirstPerson(), partialTick);
    }

    private void updateTransformation(AbstractClientPlayer player, boolean firstPersonView, float partialTick) {
        currentTransformation = animationProcessor.getTransformation(player, firstPersonView, partialTick);
    }

    @Nullable
    public BlendingModelTransform getCurrentTransformation() {
        return currentTransformation;
    }

    public void start(ID<AnimationSet> id) {
        if (AnimationSystemConfig.getInstance().isAvailable(id)) {
            animationProcessor.start(id, false);
        }
    }

    public void start(ID<AnimationSet> id, boolean mirror) {
        if (AnimationSystemConfig.getInstance().isAvailable(id)) {
            animationProcessor.start(id, mirror);
        }
    }

    public void startIfNotWorking(ID<AnimationSet> id) {
        if (AnimationSystemConfig.getInstance().isAvailable(id)) {
            animationProcessor.startIfNotWorking(id, false);
        }
    }

    public void startIfNotWorking(ID<AnimationSet> id, boolean mirror) {
        if (AnimationSystemConfig.getInstance().isAvailable(id)) {
            animationProcessor.startIfNotWorking(id, mirror);
        }
    }

    public void stop(ID<AnimationSet> id) {
        animationProcessor.stop(id);
    }

    public void stopImmediately(ID<AnimationSet> id) {
        animationProcessor.stopImmediately(id);
    }
}
