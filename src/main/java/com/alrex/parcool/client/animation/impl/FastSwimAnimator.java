package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FastSwimAnimator extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(FastSwim.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animationFactor = (getTick() + transformer.getPartialTick()) / 10;
        if (animationFactor > 1) animationFactor = 1;
        float tick = getTick() + transformer.getPartialTick();
        double armFactor = Math.cos(Math.PI * tick / 15.);
        double leftArmXAngle = 45 * armFactor;
        double rightArmXAngle = -leftArmXAngle;
        if (rightArmXAngle < 0) rightArmXAngle = -3 * Math.sqrt(-rightArmXAngle);
        if (leftArmXAngle < 0) leftArmXAngle = -3 * Math.sqrt(-leftArmXAngle);
        transformer.getRawModel().rightArm.x += Math.max(0, 2.4 * Math.sin(Math.PI * tick / 15.));
        transformer.getRawModel().rightArm.y += Math.max(0, 1.2 * Math.sin(Math.PI * tick / 15.));
        transformer.getRawModel().rightArm.z -= 1.2 * Math.sin(Math.PI * tick / 15.);
        transformer.getRawModel().leftArm.x -= Math.max(0, -2.4 * Math.sin(Math.PI * tick / 15.));
        transformer.getRawModel().leftArm.y += Math.max(0, -1.2 * Math.sin(Math.PI * tick / 15.));
        transformer.getRawModel().rightArm.z += 1.2 * Math.sin(Math.PI * tick / 15.);
        transformer.getRawModel().head.z += 0.5f * animationFactor;
        transformer
                .rotateAdditionallyHeadYaw((float) (-5 * Math.sin(Math.PI * tick / 15.)))
                .rotateRightArm((float) Math.toRadians(-190 + rightArmXAngle), 0, (float) Math.toRadians(-40 + 50 * Math.sin(Math.PI * tick / 15.)))
                .rotateLeftArm((float) Math.toRadians(-190 + leftArmXAngle), 0, (float) Math.toRadians(40 + 50 * Math.sin(Math.PI * tick / 15.)))
                .rotateRightLeg((float) Math.toRadians(-40 * Math.cos(Math.PI * tick / 9.)), 0, 0, animationFactor)
                .rotateLeftLeg((float) Math.toRadians(40 * Math.cos(Math.PI * tick / 9.)), 0, 0, animationFactor)
                .end();
    }

    @Override
    public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float tick = getTick() + rotator.getPartialTick();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
        Vec3 differenceVec =
                new Vec3(
                        lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0,
                        -lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()
                ).normalize();

        rotator.startBasedCenter()
                .rotateRollRightward((float) (-15. * Math.asin(differenceVec.z()) + 12.0 * Math.sin(Math.PI * tick / 15.)))
                .end();
    }
}
