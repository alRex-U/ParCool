package com.alrex.parcool.api.unstable.animation;

import com.alrex.parcool.client.animation.Animator;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class ParCoolAnimationEvent extends Event {
    private final AbstractClientPlayerEntity player;
    private final Animator animator;
    private final AnimationOption option;

    public ParCoolAnimationEvent(
            AbstractClientPlayerEntity player,
            Animator animator
    ) {
        this.animator = animator;
        this.player = player;
        option = new AnimationOption();
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }

    public Animator getAnimator() {
        return animator;
    }

    public AnimationOption getOption() {
        return option;
    }
}
