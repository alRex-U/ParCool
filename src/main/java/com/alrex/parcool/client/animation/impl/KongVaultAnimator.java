package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.entity.player.PlayerEntity;

import static java.lang.Math.toRadians;

public class KongVaultAnimator extends Animator {
	private static final int MAX_TIME = 11;

	float getFactor(float phase) {
		return 1 - 4 * (phase - 0.5f) * (phase - 0.5f);
	}

	float getArmFactor(float phase) {
		return phase < 0.2 ?
				1 - 25 * (phase - 0.2f) * (phase - 0.2f) :
				1 - EasingFunctions.SinInOutBySquare((phase - 0.2f) * 1.25f);
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= MAX_TIME;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MAX_TIME;
		float armFactor = getArmFactor(phase);
		float factor = getFactor(phase);
		transformer
				.rotateAdditionallyHeadPitch(-40 * armFactor)
				.rotateRightArm((float) toRadians(30 - 195 * armFactor), 0, (float) toRadians(30 - 30 * armFactor))
				.rotateLeftArm((float) toRadians(25 - 195 * armFactor), 0, (float) toRadians(-30 + 30 * armFactor))
				.rotateRightLeg((float) toRadians(-20 + 55 * factor), 0, 0)
				.rotateLeftLeg((float) toRadians(-10 + 20 * factor), 0, 0)
				.makeLegsLittleMoving()
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_TIME;
		float factor = getFactor(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(factor * 95)
				.end();
	}
}
