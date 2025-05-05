package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class ChargeJumpAnimator extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return getTick() >= ChargeJump.JUMP_ANIMATION_TICK;
    }

    @Override
    public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animationPhase = Math.min(1f, (getTick() + transformer.getPartialTick()) / ChargeJump.JUMP_ANIMATION_TICK);
        float animFactor = new Easing(animationPhase)
                .linear(0f, 8f, 1f, 1)
                .sinInOut(0.8f, 1, 1, 0)
                .get();
        float outFactor = new Easing(animationPhase)
                .sinInOut(0, 1f, 1f, 0)
                .get();
        transformer
                .translateLeftLeg(
                        0,
                        -3.4f * animFactor * Easing.with(animationPhase)
                                .squareOut(0, 0.35f, 1f, -0.25f)
                                .sinInOut(0.35f, 1f, -0.25f, 0)
                                .get(),
                        -2.6f * animFactor * Easing.with(animationPhase)
                                .squareOut(0, 0.40f, 1f, -0.4f)
                                .sinInOut(0.4f, 1f, -0.4f, 0)
                                .get()
                )
                .translateRightLeg(
                        0,
                        -1.4f * Easing.with(animationPhase)
                                .squareOut(0, 0.4f, 1, 1.2f)
                                .sinInOut(0.4f, 1, 1, 0)
                                .get(),
                        -1.6f * Easing.with(animationPhase)
                                .squareOut(0, 0.4f, 1, 1.5f)
                                .sinInOut(0.4f, 1, 1, 0)
                                .get()
                )
                .translateRightArm(
                        0.3f * outFactor,
                        Easing.with(animationPhase)
                                .squareOut(0, 0.35f, 0.4f, -1.2f)
                                .sinInOut(0.35f, 1, -1.2f, 0)
                                .get(),
                        0
                )
                .translateLeftArm(
                        -0.3f * outFactor,
                        Easing.with(animationPhase)
                                .squareOut(0, 0.35f, 0.4f, -1.2f)
                                .sinInOut(0.35f, 1, -1.2f, 0)
                                .get(),
                        0
                )
                .rotateLeftLeg(
                        (float) Math.toRadians(
                                -15 + 55 * Easing.with(animationPhase)
                                        .squareOut(0, 0.4f, 0, 1)
                                        .sinInOut(0.4f, 1, 1, 0.15f)
                                        .get()
                        ),
                        0, 0, animFactor
                )
                .rotateRightLeg(
                        (float) Math.toRadians(
                                10 - 40 * Easing.with(animationPhase)
                                        .squareOut(0, 0.25f, 0f, 1)
                                        .sinInOut(0.25f, 1, 1, 0.25f)
                                        .get()
                        ),
                        0, 0, animFactor
                )
                .rotateLeftArm(
                        (float) Math.toRadians(
                                Easing.with(animationPhase)
                                        .squareOut(0, 0.35f, 6, -190)
                                        .linear(0.35f, 0.5f, -180, -180)
                                        .sinInOut(0.5f, 1, -180, 0)
                                        .get()
                        ),
                        0,
                        (float) Math.toRadians(
                                Easing.with(animationPhase)
                                        .squareOut(0, 0.35f, -4, 2)
                                        .squareIn(0.35f, 0.85f, 5, -15)
                                        .squareOut(0.85f, 1f, -15, 0)
                                        .get()
                        )
                )
                .rotateRightArm(
                        (float) Math.toRadians(
                                Easing.with(animationPhase)
                                        .squareOut(0, 0.35f, -22, -190)
                                        .linear(0.35f, 0.5f, -180, -180)
                                        .sinInOut(0.5f, 1, -180, 0)
                                        .get()
                        ),
                        0,
                        (float) Math.toRadians(
                                Easing.with(animationPhase)
                                        .squareOut(0, 0.35f, 4, -2)
                                        .squareIn(0.35f, 0.85f, -5, 15)
                                        .squareOut(0.85f, 1f, 15, 0)
                                        .get()
                        )
                )
                .makeArmsNatural()
                .rotateAdditionallyHeadPitch(
                        Easing.with(animationPhase)
                                .sinInOut(0, 1, -45f, 0)
                                .get()
                );
        return true;
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float animationPhase = Math.min(1f, (getTick() + rotator.getPartialTick()) / ChargeJump.JUMP_ANIMATION_TICK);
        float animFactor = new Easing(animationPhase)
                .linear(0f, 8f, 1f, 1)
                .sinInOut(0.8f, 1, 1, 0)
                .get();
        rotator
                .rotateYawRightward(180f + rotator.getYRot())
                .translate(0, 0f,
                        0.3f * animFactor * Easing.with(animationPhase)
                                .linear(0, 1, 1, 0).get()
                )
                .rotatePitchFrontward(
                        25 * animFactor * Easing.with(animationPhase)
                                .squareOut(0, 0.45f, 1, -0.2f)
                                .linear(0.45f, 0.55f, -0.2f, -0.2f)
                                .squareIn(0.55f, 1, -0.2f, 0)
                                .get()
                );
        return true;
    }
}
