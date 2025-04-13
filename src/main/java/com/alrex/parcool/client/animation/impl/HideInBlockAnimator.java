package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class HideInBlockAnimator extends Animator {
    @Override
    public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
        return !parkourability.get(HideInBlock.class).isDoing();
    }

    @Override
    public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = (getTick() + transformer.getPartialTick()) / 5f;
        if (phase > 1f) phase = 1f;
        float factor = Easing.with(phase).sinInOut(0f, 1f, 0, 1f).get();
        transformer.rotateHeadPitch(-40 * factor)
                .rotateHeadYawRadian((float) (MathHelper.clamp(transformer.getRawModel().getHead().yRot, -Math.PI / 5., Math.PI / 5.) / 2.))
                .rotateLeftArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(-160), factor)
                .rotateRightArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(160), factor)
                .translateLeftLeg(0, -0.8f * factor, -0.7f * factor)
                .translateRightLeg(0, -0.8f * factor, -0.7f * factor)
                .rotateLeftLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(-10), factor)
                .rotateRightLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(10), factor);
    }

    @Override
    public boolean rotatePre(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = (getTick() + rotator.getPartialTick()) / 5f;
        if (phase > 1f) phase = 1f;
        float factor = Easing.with(phase).sinInOut(0f, 1f, 0, 1f).get();
        Vector3d vec = parkourability.get(HideInBlock.class).getLookDirection();
        if (vec == null) return false;
        float yRot = (float) VectorUtil.toYawDegree(vec);
        rotator.rotateYawRightward(180f + yRot)
                .rotatePitchFrontward(90f * factor)
                .translate(0, -0.95f * factor, 0.3f * factor);
        return true;
    }
}
