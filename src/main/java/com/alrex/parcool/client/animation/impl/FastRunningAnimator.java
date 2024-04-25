package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FastRunningAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(FastRun.class).isDoing();
	}

	private float bodyAngleFactor(float phase) {
		return new Easing(phase)
				.squareOut(0, 1, 0, 1)
				.get();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngleFactor = bodyAngleFactor(phase);
		double rightXRotFactor = Math.cos(transformer.getLimbSwing() * 0.6662 + Math.PI);
		double leftXRotFactor = Math.cos(transformer.getLimbSwing() * 0.6662);
		HandSide attackHand = transformer.getRawModel().getAttackArm(player);
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
			transformer.getRawModel().leftArm.z += (float) (leftXRotFactor * 2);
			transformer.getRawModel().leftArm.x -= (float) (Math.abs(leftXRotFactor));
			transformer.getRawModel().leftArm.y += bodyAngleFactor * 0.8f;
		}

		if (rightArmAnimatable) {
			transformer.getRawModel().rightArm.z += (float) (rightXRotFactor * 2);
			transformer.getRawModel().rightArm.x += (float) (Math.abs(rightXRotFactor));
			transformer.getRawModel().rightArm.y += bodyAngleFactor * 0.8f;
		}

		transformer.getRawModel().rightLeg.y += (float) (Math.min(0, transformer.getRawModel().rightLeg.xRot / (Math.PI / 2.)));
		transformer.getRawModel().rightLeg.z += (float) (transformer.getRawModel().rightLeg.xRot / (Math.PI / 3.));

		transformer.getRawModel().leftLeg.y += (float) (Math.min(0, transformer.getRawModel().leftLeg.xRot / (Math.PI / 2.)));
		transformer.getRawModel().leftLeg.z += (float) (transformer.getRawModel().leftLeg.xRot / (Math.PI / 3.));

		float tick = getTick() + transformer.getPartialTick();
		if (leftArmAnimatable) {
			transformer
					.rotateLeftArm(
							(float) (Math.toRadians(-20 * bodyAngleFactor) + leftXRotFactor * transformer.getLimbSwingAmount()),
							0,
							(float) Math.toRadians(
									bodyAngleFactor * -15
											+ (1. + Math.cos(transformer.getLimbSwing() * 1.3324)) / 2. * Math.sin(transformer.getLimbSwing() * 1.3324) * -17
							)
					);
		}
		if (rightArmAnimatable) {
			transformer
					.rotateRightArm(
							(float) (Math.toRadians(-20 * bodyAngleFactor) + rightXRotFactor * transformer.getLimbSwingAmount()),
							0,
							(float) Math.toRadians(
									bodyAngleFactor * 15
											+ (1. + Math.cos(transformer.getLimbSwing() * 1.3324 + Math.PI)) / 2. * Math.sin(transformer.getLimbSwing() * 1.3324 + Math.PI) * 17
							)
					);
		}
		transformer
				.rotateAdditionallyHeadPitch(bodyAngleFactor * -30 - 5f * (float) Math.sin(Math.PI * tick / 10))
				.addRotateRightLeg((float) Math.toRadians(-15 * bodyAngleFactor), 0, 0)
				.addRotateLeftLeg((float) Math.toRadians(-15 * bodyAngleFactor), 0, 0)
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float tick = getTick() + rotator.getPartialTick();
		float phase = tick / 10;
		if (phase > 1) phase = 1;
		float pitch = bodyAngleFactor(phase) * 25 + 5f * (float) Math.sin(Math.PI * tick / 10);
		if (parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableLeanAnimationOfFastRun)) {
			if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
				rotator
						.startBasedCenter()
						.rotatePitchFrontward(pitch)
						.end();
			} else {
				Vec3 lookAngle = player.getLookAngle();
				Vec3 bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
				Vec3 differenceVec =
						new Vec3(
								lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0,
								-lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()
						).normalize();
				rotator
						.startBasedCenter()
						.rotatePitchFrontward(pitch)
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