package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.entity.player.PlayerEntity;

public class DiveIntoWaterAnimator extends Animator {
    private static final int MAX_ANIMATION_TICK = 9;
    private final boolean fromSkyDive;

    public DiveIntoWaterAnimator(boolean fromSkyDive) {
        this.fromSkyDive = fromSkyDive;
    }

    @Override
    public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
        return getTick() >= MAX_ANIMATION_TICK;
    }

    @Override
    public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = (getTick() + transformer.getPartialTick()) / MAX_ANIMATION_TICK;
        if (phase > 1) return;
        float animationFactor = new Easing(phase)
                .sinInOut(0, 0.1f, 0, 1)
                .linear(0.1f, 0.7f, 1, 1)
                .sinInOut(0.7f, 1, 1, 0)
                .get();
        float headPitchFactor = new Easing(phase)
                .squareOut(0, 0.5f, 0, 1)
                .sinInOut(0.5f, 1, 1, 0)
                .get();
        float legPitchFactor = new Easing(phase)
                .squareOut(0, 0.65f, 0, 1)
                .sinInOut(0.65f, 1, 1, 0)
                .get();
        float armRollFactor = new Easing(phase)
                .squareOut(0, 1, 1, 0)
                .get();
        transformer
                .rotateLeftArm(0, 0, (float) Math.toRadians(-170 * armRollFactor), animationFactor)
                .rotateRightArm(0, 0, (float) Math.toRadians(170 * armRollFactor), animationFactor)
                .rotateLeftLeg((float) Math.toRadians(-55 * legPitchFactor), 0, 0, animationFactor)
                .rotateRightLeg((float) Math.toRadians(20 - 75 * legPitchFactor), 0, 0, animationFactor)
                .makeLegsLittleMoving()
                .makeArmsNatural()
                .rotateAdditionallyHeadPitch((float) Math.toRadians(30 * headPitchFactor));
    }

    @Override
    public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = (getTick() + rotator.getPartialTick()) / MAX_ANIMATION_TICK;
        if (phase > 1) return;
        float pitchFactor = new Easing(phase).squareOut(0, 1, 0, 1).get();
        rotator.startBasedCenter()
                .rotatePitchFrontward(
                        fromSkyDive ? 90f + 270f * pitchFactor : 180f + 180f * pitchFactor
                )
                .end();
    }
}
