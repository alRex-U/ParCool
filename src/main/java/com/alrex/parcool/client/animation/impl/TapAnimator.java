package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.world.entity.player.Player;

;

public class TapAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Tap.class).isDoing();
	}

	public float angleFactor(float phase) {
		if (phase < 0.3) {
			return 1 - 11 * (phase - 0.3f) * (phase - 0.3f);
		} else if (phase < 0.7) {
			return 1;
		} else {
			return 1 - EasingFunctions.SinInOutBySquare((phase - 0.7f) * 3.3f);
		}
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / parkourability.get(Tap.class).getMaxTappingTick();
		float factor = angleFactor(phase);
		float angle = 80 * factor;
		transformer
				.rotateLeftLeg(
						(float) Math.toRadians(-angle),
						0, 0
				)
				.rotateRightLeg(
						(float) Math.toRadians(-angle),
						0, 0
				)
				.rotateRightArm(
						(float) Math.toRadians(-angle),
						0,
						(float) Math.toRadians(factor * 20)
				)
				.rotateLeftArm(
						(float) Math.toRadians(-angle),
						0,
						(float) Math.toRadians(-factor * 20)
				)
				.makeArmsNatural()
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / parkourability.get(Tap.class).getMaxTappingTick();
		float angle = 80 * angleFactor(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(angle)
				.endEnabledLegGrounding();
	}
}
