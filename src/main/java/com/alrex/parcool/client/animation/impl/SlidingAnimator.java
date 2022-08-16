package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class SlidingAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getCrawl().isSliding();
	}

	private float bodyAngleFactor(float phase) {
		if (phase < 0.4f) {
			return 1 - 6.25f * (phase - 0.4f) * (phase - 0.4f);
		}
		return 1;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		transformer
				.rotateHeadPitch(50)
				.rotateRightArm((float) Math.toRadians(145), 0, (float) Math.toRadians(-30))
				.rotateLeftArm((float) Math.toRadians(150), 0, (float) Math.toRadians(35))
				.rotateRightLeg((float) Math.toRadians(-7), 0, (float) Math.toRadians(-5))
				.rotateLeftLeg((float) Math.toRadians(10), 0, (float) Math.toRadians(15))
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float swimAmount = player.getSwimAmount(rotator.getPartialTick());
		rotator
				.startBasedCenter()
				.rotateFrontward(-80 * bodyAngleFactor(swimAmount) - 90 * swimAmount)
				.end();
	}
}
