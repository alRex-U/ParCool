package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class JumpChargingAnimator extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        ChargeJump c = parkourability.get(ChargeJump.class);
        return !c.isCharging();
    }

    @Override
    public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float transitionPhase = Math.min(1f, (parkourability.get(ChargeJump.class).getChargingTick() + transformer.getPartialTick()) / ChargeJump.JUMP_CHARGE_TICK);
        float animFactor = new Easing(transitionPhase)
                .sinInOut(0, 1, 0, 1)
                .get();
        transformer.getRawModel().leftLeg.z -= 2.6f * animFactor;
        transformer.getRawModel().leftLeg.y -= 3.4f * animFactor;
        transformer.getRawModel().rightLeg.z -= 1.4f * animFactor;
        transformer.getRawModel().rightLeg.y -= 1.6f * animFactor;
        transformer.getRawModel().rightArm.x += 0.3f * animFactor;
        transformer.getRawModel().rightArm.y += 0.4f * animFactor;
        transformer.getRawModel().leftArm.x -= 0.3f * animFactor;
        transformer.getRawModel().leftArm.y += 0.4f * animFactor;
        transformer
                .rotateLeftLeg((float) Math.toRadians(-15), 0, 0, animFactor)
                .rotateRightLeg((float) Math.toRadians(10), 0, 0, animFactor)
                .rotateLeftArm((float) Math.toRadians(6), 0, (float) Math.toRadians(-4), animFactor)
                .rotateRightArm((float) Math.toRadians(-22), 0, (float) Math.toRadians(4), animFactor)
                .makeArmsNatural()
                .rotateAdditionallyHeadPitch(-45 * animFactor);
        return true;
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float transitionPhase = Math.min(1f, (parkourability.get(ChargeJump.class).getChargingTick() + rotator.getPartialTick()) / ChargeJump.JUMP_CHARGE_TICK);
        float animFactor = new Easing(transitionPhase)
                .sinInOut(0, 1, 0, 1)
                .get();
        rotator
                .rotateYawRightward(180f + rotator.getYRot())
                .translate(0, 0f, 0.3f * animFactor)
                .rotatePitchFrontward(25 * animFactor);
        return true;
    }
}
