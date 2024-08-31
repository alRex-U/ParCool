package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
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

        HumanoidArm attackHand = player.getMainArm();
        boolean leftArmAnimatable = attackHand != HumanoidArm.LEFT || transformer.getRawModel().attackTime <= 0f;
        boolean rightArmAnimatable = attackHand != HumanoidArm.RIGHT || transformer.getRawModel().attackTime <= 0f;
        if (leftArmAnimatable && ((
                transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.EMPTY
                        && transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.ITEM
        )
                || transformer.getRawModel().rightArmPose.isTwoHanded()
        )
        ) {
            leftArmAnimatable = false;
        }
        if (rightArmAnimatable && ((
                transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.EMPTY
                        && transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.ITEM
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
            transformer.translateRightArm(
                    (float) Math.max(0, 2.4 * Math.sin(Math.PI * tick / 15.)),
                    (float) Math.max(0, 1.2 * Math.sin(Math.PI * tick / 15.)),
                    (float) (-1.2 * Math.sin(Math.PI * tick / 15.))
            );
        }

        if (leftArmAnimatable) {
            transformer.translateLeftArm(
                    -(float) Math.max(0, -2.4 * Math.sin(Math.PI * tick / 15.)),
                    (float) Math.max(0, -1.2 * Math.sin(Math.PI * tick / 15.)),
                    (float) (1.2 * Math.sin(Math.PI * tick / 15.))
            );
        }

        transformer
                .translateHead(0, 0, 0.5f * animationFactor)
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
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
            return;
        }
        float tick = getTick() + rotator.getPartialTick();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
        Vec3 differenceVec =
                new Vec3(
                        lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0,
                        -lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()
                ).normalize();

        rotator.startBasedCenter()
                .rotateYawRightward((float) (-15. * Math.asin(differenceVec.z()) + 12.0 * Math.sin(Math.PI * tick / 15.)))
                .end();
    }
}
