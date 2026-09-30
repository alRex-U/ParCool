package com.alrex.parcool.api.unstable.animation;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.fabric.Event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;

@Environment(EnvType.CLIENT)
public class ParCoolAnimationInfoEvent extends Event {
    private final AbstractClientPlayer player;
    private final Animator animator;
    private final AnimationOption option;

    public ParCoolAnimationInfoEvent(AbstractClientPlayer player, Animator animator) {
        this.animator = animator;
        this.player = player;
        option = new AnimationOption();
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public Animator getAnimator() {
        return animator;
    }

    public AnimationOption getOption() {
        return option;
    }
}
