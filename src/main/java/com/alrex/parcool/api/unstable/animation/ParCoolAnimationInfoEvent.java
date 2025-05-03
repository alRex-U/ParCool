package com.alrex.parcool.api.unstable.animation;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.compatibility.AbstractClientPlayerWrapper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class ParCoolAnimationInfoEvent extends Event {
    private final AbstractClientPlayerWrapper player;
    private final Animator animator;
    private final AnimationOption option;

    public ParCoolAnimationInfoEvent(
            AbstractClientPlayerWrapper player,
            Animator animator
    ) {
        this.animator = animator;
        this.player = player;
        option = new AnimationOption();
    }

    public AbstractClientPlayerWrapper getPlayer() {
        return player;
    }

    public Animator getAnimator() {
        return animator;
    }

    public AnimationOption getOption() {
        return option;
    }
}
