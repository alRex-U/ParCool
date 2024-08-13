package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.BipedModelUtil;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;

public class FastSwimAnimator extends Animator {
    @Override
    public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
        return !parkourability.get(FastSwim.class).isDoing();
    }

    @Override
    public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animationFactor = (getTick() + transformer.getPartialTick()) / 10;
        if (animationFactor > 1) animationFactor = 1;
        float tick = getTick() + transformer.getPartialTick();
        double armFactor = Math.cos(Math.PI * tick / 15.);

        HandSide attackHand = BipedModelUtil.getAttackArm(player);
        boolean leftArmAnimatable = attackHand != HandSide.LEFT || transformer.getRawModel().attackTime <= 0f;
        boolean rightArmAnimatable = attackHand != HandSide.RIGHT || transformer.getRawModel().attackTime <= 0f;
        if (leftArmAnimatable && ((
                transformer.getRawModel().leftArmPose != BipedModel.ArmPose.EMPTY
                        && transformer.getRawModel().leftArmPose != BipedModel.ArmPose.ITEM
        )
                || transformer.getRawModel().rightArmPose.isTwoHanded()
        )
        ) {
            leftArmAnimatable = false;
        }
        if (rightArmAnimatable && ((
                transformer.getRawModel().rightArmPose != BipedModel.ArmPose.EMPTY
                        && transformer.getRawModel().rightArmPose != BipedModel.ArmPose.ITEM
        )
                || transformer.getRawModel().leftArmPose.isTwoHanded()
        )
        ) {
            rightArmAnimatable = false;
        }

        double leftArmXAngle = 45 * armFactor;
        double rightArmXAngle = -leftArmXAngle;
        if (rightArmXAngle < 0) rightArmXAngle = -3 * Math.sqrt(-rightArmXAngle);
        if (leftArmXAngle < 0) leftArmXAngle = -3 * Math.sqrt(-leftArmXAngle);
        if (rightArmAnimatable) {
            transformer.getRawModel().rightArm.x += (float) Math.max(0, 2.4 * Math.sin(Math.PI * tick / 15.));
            transformer.getRawModel().rightArm.y += (float) Math.max(0, 1.2 * Math.sin(Math.PI * tick / 15.));
            transformer.getRawModel().rightArm.z -= (float) (1.2 * Math.sin(Math.PI * tick / 15.));
        }

        if (leftArmAnimatable) {
            transformer.getRawModel().leftArm.x -= (float) Math.max(0, -2.4 * Math.sin(Math.PI * tick / 15.));
            transformer.getRawModel().leftArm.y += (float) Math.max(0, -1.2 * Math.sin(Math.PI * tick / 15.));
            transformer.getRawModel().leftArm.z += (float) (1.2 * Math.sin(Math.PI * tick / 15.));
        }

        transformer.getRawModel().head.z += 0.5f * animationFactor;
        transformer
                .rotateAdditionallyHeadYaw((float) (-5 * Math.sin(Math.PI * tick / 15.)))
                .rotateRightArm((float) Math.toRadians(-190 + rightArmXAngle), 0, (float) Math.toRadians(-40 + 50 * Math.sin(Math.PI * tick / 15.)))
                .rotateLeftArm((float) Math.toRadians(-190 + leftArmXAngle), 0, (float) Math.toRadians(40 + 50 * Math.sin(Math.PI * tick / 15.)));
        if (rightArmAnimatable) {
            transformer.rotateRightLeg((float) Math.toRadians(-40 * Math.cos(Math.PI * tick / 9.)), 0, 0, animationFactor);
        }
        if (leftArmAnimatable) {
            transformer.rotateLeftLeg((float) Math.toRadians(40 * Math.cos(Math.PI * tick / 9.)), 0, 0, animationFactor);
        }
        transformer.end();
    }

    @Override
    public void rotatePost(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
        if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
            return;
        }
        float tick = getTick() + rotator.getPartialTick();
        Vector3d lookAngle = player.getLookAngle();
        Vector3d bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
        Vector3d differenceVec =
                new Vector3d(
                        lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0,
                        -lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()
                ).normalize();

        rotator.startBasedCenter()
                .rotateRollRightward((float) (-15. * Math.asin(differenceVec.z()) + 12.0 * Math.sin(Math.PI * tick / 15.)))
                .end();
    }
}
