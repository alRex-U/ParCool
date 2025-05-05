package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HideInBlockAnimator extends Animator {
    private final boolean standing;
    private final boolean startFromDiving;

    public HideInBlockAnimator(boolean standing, boolean startFromDiving) {
        this.standing = standing;
        this.startFromDiving = startFromDiving;
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(HideInBlock.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = (getTick() + transformer.getPartialTick()) / (startFromDiving ? 2f : 5f);
        if (phase > 1f) phase = 1f;
        float factor = Easing.with(phase).sinInOut(0f, 1f, 0, 1f).get();
        if (standing) {
            transformer.rotateHeadYawRadian((float) (Mth.clamp(transformer.getRawModel().getHead().yRot, -Math.PI / 4., Math.PI / 4.) / 1.4f))
                    .rotateRightArm(0, 0, (float) Math.toRadians(6), factor)
                    .rotateLeftArm(0, 0, (float) Math.toRadians(-6), factor)
                    .makeArmsNatural()
                    .rotateRightLeg(0, 0, 0)
                    .rotateLeftLeg(0, 0, 0);
        } else {
            transformer.rotateHeadPitch(-40 * factor)
                    .rotateHeadYawRadian((float) (Mth.clamp(transformer.getRawModel().getHead().yRot, -Math.PI / 5., Math.PI / 5.) / 2.))
                    .rotateLeftArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(-160), factor)
                    .rotateRightArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(160), factor)
                    .translateLeftLeg(0, -0.8f * factor, -0.7f * factor)
                    .translateRightLeg(0, -0.8f * factor, -0.7f * factor)
                    .rotateLeftLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(-10), factor)
                    .rotateRightLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(10), factor);
        }
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = (getTick() + rotator.getPartialTick()) / 5f;
        if (phase > 1f) phase = 1f;
        float factor = Easing.with(phase).sinInOut(0f, 1f, 0, 1f).get();
        Vec3 lookVec = parkourability.get(HideInBlock.class).getLookDirection();
        if (lookVec == null) return false;
        if (standing) {
            float playerYRot = 180f + Mth.lerp(rotator.getPartialTick(), player.yRotO, player.getYRot());
            float yRot = (float) VectorUtil.toYawDegree(lookVec);
            rotator.rotateYawRightward(playerYRot + MathUtil.normalizeDegree((180f + yRot) - playerYRot) * factor);
            return true;
        } else {
            float yRot = (float) VectorUtil.toYawDegree(lookVec);
            rotator.rotateYawRightward(180f + yRot)
                    .rotatePitchFrontward(startFromDiving ? 180f - 90f * factor : (90f * factor))
                    .translate(0, -0.95f * factor, 0.3f * factor);
            return true;
        }
    }
}
