package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.event.AnimationUpdateEvent;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
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

    public boolean isFirstPersonViewPlayer() {
        return Minecraft.getInstance().getCameraEntity() == animationProcessor.getOwner()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public boolean cancelPlayerModelRotation() {
        return isFirstPersonViewPlayer();
    }

    public void onRenderTick(float partialTick) {
        updateTransformation(isFirstPersonViewPlayer(), partialTick);
    }

    private void updateTransformation(boolean firstPersonView, float partialTick) {
        if (firstPersonView && !AnimationSystemConfig.getInstance().enableFPVAnimation.get()) return;

        var preEvent = new AnimationUpdateEvent.Pre(animationProcessor.getOwner(), animationProcessor);
        var cancelled = MinecraftForge.EVENT_BUS.post(preEvent);
        if (preEvent.isAnimationStopped()) currentTransformation = null;
        if (cancelled) return;

        if (AnimationSystemConfig.getInstance().enableAnimation.get()) {
            currentTransformation = animationProcessor.getTransformation(firstPersonView, partialTick);
            if (currentTransformation != null) {
                MinecraftForge.EVENT_BUS.post(new AnimationUpdateEvent.Post(
                        animationProcessor.getOwner(),
                        animationProcessor,
                        currentTransformation.transformation()
                ));
            }
        } else currentTransformation = null;
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

    public boolean isIdle() {
        return animationProcessor.isIdle();
    }
}
