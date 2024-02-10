package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.entity.player.PlayerEntity;

public class FastRunningAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(FastRun.class).isDoing();
	}

	private float bodyAngleFactor(float phase) {
		return new Easing(phase)
				.squareOut(0, 1, 0, 1)
				.get();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngleFactor = bodyAngleFactor(phase);
		float leftZFactor = (float) (1 - Math.abs(transformer.getRawModel().leftArm.xRot) / (Math.PI / 3));
		float rightZFactor = (float) (1 - Math.abs(transformer.getRawModel().rightArm.xRot) / (Math.PI / 3));
		transformer.getRawModel().leftArm.z = (float) (transformer.getRawModel().leftArm.xRot / (Math.PI / 4) * 2);
		transformer.getRawModel().rightArm.z = (float) (transformer.getRawModel().rightArm.xRot / (Math.PI / 4) * 2);
		transformer.getRawModel().leftArm.x -= (float) (Math.abs(transformer.getRawModel().leftArm.xRot) / (Math.PI / 3));
		transformer.getRawModel().rightArm.x += (float) (Math.abs(transformer.getRawModel().rightArm.xRot) / (Math.PI / 3));
		transformer.getRawModel().leftArm.y += bodyAngleFactor * 0.8f;
		transformer.getRawModel().rightArm.y += bodyAngleFactor * 0.8f;
		float tick = getTick() + transformer.getPartialTick();
		transformer
				.addRotateRightArm((float) Math.toRadians(-20 * bodyAngleFactor), 0, (float) Math.toRadians(bodyAngleFactor * 5 + rightZFactor * 20))
				.addRotateLeftArm((float) Math.toRadians(-20 * bodyAngleFactor), 0, (float) Math.toRadians(bodyAngleFactor * -5 + leftZFactor * -20))
				.rotateAdditionallyHeadPitch(bodyAngleFactor * -30 - 5f * (float) Math.sin(Math.PI * tick / 10))
				.addRotateRightLeg((float) Math.toRadians(-25 * bodyAngleFactor), 0, 0)
				.addRotateLeftLeg((float) Math.toRadians(-25 * bodyAngleFactor), 0, 0)
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float tick = getTick() + rotator.getPartialTick();
		float bodyAngle = bodyAngleFactor(phase) * 25 + 5f * (float) Math.sin(Math.PI * tick / 10);
		rotator
				.startBasedCenter()
				.rotatePitchFrontward(bodyAngle)
				.end();
	}
}