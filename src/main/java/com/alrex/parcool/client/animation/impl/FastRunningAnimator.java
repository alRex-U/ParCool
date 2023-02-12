package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.entity.player.PlayerEntity;

public class FastRunningAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getFastRun().isDoing();
	}

	private float bodyAngleFactor(float phase) {
		return EasingFunctions.SinInOutBySquare(phase);
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (parkourability.getFastRun().getDoingTick() + transformer.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngleFactor = bodyAngleFactor(phase);
		transformer
				.addRotateRightArm((float) Math.toRadians(-10 * bodyAngleFactor), 0, (float) Math.toRadians(bodyAngleFactor * 10))
				.addRotateLeftArm((float) Math.toRadians(-10 * bodyAngleFactor), 0, (float) Math.toRadians(bodyAngleFactor * -10))
				.rotateAdditionallyHeadPitch(bodyAngleFactor * -20)
				.makeArmsNatural()
				.addRotateRightLeg((float) Math.toRadians(-20 * bodyAngleFactor), 0, 0)
				.addRotateLeftLeg((float) Math.toRadians(-20 * bodyAngleFactor), 0, 0)
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (parkourability.getFastRun().getDoingTick() + rotator.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngle = bodyAngleFactor(phase) * 20;
		rotator
				.startBasedCenter()
				.rotateFrontward(bodyAngle)
				.end();
	}
}