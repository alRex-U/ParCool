package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

;

public class SlidingAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Slide.class).isDoing();
	}

	private float bodyAngleFactor(float phase) {
		if (phase < 0.4f) {
			return 1 - 6.25f * (phase - 0.4f) * (phase - 0.4f);
		}
		return 1;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		transformer
				.rotateHeadPitch(50)
				.rotateRightArm((float) Math.toRadians(45), 0, (float) Math.toRadians(110))
				.rotateLeftArm((float) Math.toRadians(50), 0, (float) Math.toRadians(-100))
				.rotateRightLeg((float) Math.toRadians(-17), 0, (float) Math.toRadians(-5))
				.rotateLeftLeg((float) Math.toRadians(-5), 0, (float) Math.toRadians(15))
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float swimAmount = player.getSwimAmount(rotator.getPartialTick());
		rotator
				.startBasedCenter()
				.rotateFrontward(-70 * bodyAngleFactor(swimAmount) - 90 * swimAmount)
				.end();
	}
}
