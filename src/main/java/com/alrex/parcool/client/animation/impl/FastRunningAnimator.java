package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.world.entity.player.Player;

public class FastRunningAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.getFastRun().isRunning();
	}

	private float bodyAngleFactor(float phase) {
		return EasingFunctions.SinInOutBySquare(phase);
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (parkourability.getFastRun().getRunningTick() + transformer.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngleFactor = bodyAngleFactor(phase);
		transformer
				.addRotateRightArm(0, 0, (float) Math.toRadians(bodyAngleFactor * 10))
				.addRotateLeftArm(0, 0, (float) Math.toRadians(bodyAngleFactor * -10))
				.rotateAdditionallyHeadPitch(bodyAngleFactor * -20)
				.makeArmsNatural()
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (parkourability.getFastRun().getRunningTick() + rotator.getPartialTick()) / 10;
		if (phase > 1) phase = 1;
		float bodyAngle = bodyAngleFactor(phase) * 20;
		rotator
				.startBasedCenter()
				.rotateFrontward(bodyAngle)
				.end();
	}
}