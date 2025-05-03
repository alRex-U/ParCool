package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BipedModelUtil;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.HandSide;

public class FastRunningAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability) {
		return !parkourability.get(FastRun.class).isDoing();
	}

	private float bodyAngleFactor(float phase) {
		return new Easing(phase)
				.squareOut(0, 1, 0, 1)
				.get();
	}

	private float limbSwing = 0;
	@Override
	public void animatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelTransformer transformer) {
		limbSwing = transformer.getLimbSwing();
		float phase = (getTick() + transformer.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngleFactor = bodyAngleFactor(phase);
		double rightXRotFactor = Math.cos(limbSwing * 0.6662 + Math.PI);
		double leftXRotFactor = Math.cos(limbSwing * 0.6662);
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

		if (leftArmAnimatable) {
            transformer.translateLeftArm(
                    (float) (-Math.abs(leftXRotFactor)),
                    bodyAngleFactor * 0.8f,
                    (float) (leftXRotFactor * 2.)
            );
		}

		if (rightArmAnimatable) {
            transformer.translateRightArm(
                    (float) (Math.abs(rightXRotFactor)),
                    bodyAngleFactor * 0.8f,
                    (float) (rightXRotFactor * 2)
            );
		}

        transformer
                .translateRightLeg(
                        0,
                        (float) (Math.min(0, transformer.getRawModel().rightLeg.xRot / (Math.PI / 2.))),
                        (float) (transformer.getRawModel().rightLeg.xRot / (Math.PI / 3.))
                )
                .translateLeftLeg(
                        0,
                        (float) (Math.min(0, transformer.getRawModel().leftLeg.xRot / (Math.PI / 2.))),
                        (float) (transformer.getRawModel().leftLeg.xRot / (Math.PI / 3.))
                );

		float bodyYaw = (float) (10. * Math.cos(limbSwing * 0.6662));
		float tick = getTick() + transformer.getPartialTick();
		if (leftArmAnimatable) {
			transformer
					.rotateLeftArm(
							(float) (Math.toRadians(-25 * bodyAngleFactor) + 1.2 * leftXRotFactor * transformer.getLimbSwingAmount()),
							0,
							(float) Math.toRadians(
									bodyAngleFactor * -15
											+ (0.65 + Math.cos(limbSwing * 1.3324)) / 2. * Math.sin(limbSwing * 1.3324) * -30
							)
					);
		}
		if (rightArmAnimatable) {
			transformer
					.rotateRightArm(
							(float) (Math.toRadians(-25 * bodyAngleFactor) + 1.2 * rightXRotFactor * transformer.getLimbSwingAmount()),
							0,
							(float) Math.toRadians(
									bodyAngleFactor * 15
											+ (0.65 + Math.cos(limbSwing * 1.3324 + Math.PI)) / 2. * Math.sin(limbSwing * 1.3324 + Math.PI) * 30
							)
					);
		}
		transformer
				.rotateAdditionallyHeadPitch(bodyAngleFactor * -30 - 5f * (float) Math.sin(Math.PI * tick / 10))
				.rotateAdditionallyHeadYaw(bodyYaw)
				.addRotateRightLeg(
						(float) Math.toRadians(-15 * bodyAngleFactor),
						(float) Math.toRadians(bodyYaw),
						0
				)
				.addRotateLeftLeg(
						(float) Math.toRadians(-15 * bodyAngleFactor),
						(float) Math.toRadians(bodyYaw),
						0
				)
				.end();
	}

	@Override
    public void rotatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelRotator rotator) {
		float tick = getTick() + rotator.getPartialTick();
		float phase = tick / 10;
		if (phase > 1) phase = 1;
		float bodyYaw = (float) (-10. * Math.cos(limbSwing * 0.6662));
		float pitch = bodyAngleFactor(phase) * 30 + 5f * (float) Math.sin(Math.PI * tick / 10);
		float yOffset = 0.145f * (float) Math.pow(Math.cos(limbSwing * 0.6662), 2.);
		rotator.translateY(yOffset);
		if (parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableLeanAnimationOfFastRun)) {
			if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
				rotator
						.startBasedCenter()
						.rotatePitchFrontward(pitch)
						.end();
			} else {
				Vec3Wrapper lookAngle = player.getLookAngle();
				Vec3Wrapper bodyAngle = player.getRotatedBodyAngle(rotator);
				Vec3Wrapper differenceVec =
						new Vec3Wrapper(
								lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0,
								-lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()
						).normalize();
				rotator
						.startBasedCenter()
						.rotatePitchFrontward(pitch)
						.rotateYawRightward(bodyYaw)
						.rotateRollRightward((float) (30. * phase * Math.asin(differenceVec.z())))
						.end();
			}
		} else {
			rotator
					.startBasedCenter()
					.rotatePitchFrontward(pitch)
					.end();
		}
    }
}