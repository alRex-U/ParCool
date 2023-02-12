package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

import static com.alrex.parcool.utilities.MathUtil.lerp;

public class DodgeAnimator extends Animator {
	private boolean notInitialized = true;
	private boolean frontLeg = false;

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getDodge().isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / 6f;
		if (phase > 1) {
			return;
		}
		float factor = factorFunc(phase);
		float revision = -lerp(0, 30, factor);
		switch (parkourability.getDodge().getDodgeDirection()) {
			case Front:
				transformer.rotateAdditionallyHeadPitch(revision);
				if (notInitialized) {
					frontLeg = player.getRandom().nextBoolean();
					notInitialized = false;
				}
				if (frontLeg) {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(-lerp(10, 35, factor) + revision),
									0, 0
							)
							.rotateLeftLeg(
									(float) Math.toRadians(lerp(30, 60, factor) + revision),
									0, 0
							)
							.end();
				} else {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(lerp(30, 60, factor) + revision),
									0, 0
							)
							.rotateLeftLeg(
									(float) Math.toRadians(-lerp(10, 35, factor) + revision),
									0, 0
							)
							.end();
				}
				break;
			case Right:
				transformer
						.rotateAdditionallyHeadPitch(revision)
						.rotateRightLeg(
								(float) Math.toRadians(-lerp(10, 35, factor) + revision),
								0, 0
						)
						.rotateLeftLeg(
								(float) Math.toRadians(lerp(30, 60, factor) + revision),
								0, 0
						)
						.end();
				break;
			case Left:
				transformer
						.rotateAdditionallyHeadPitch(revision)
						.rotateRightLeg(
								(float) Math.toRadians(lerp(30, 60, factor) + revision),
								0, 0
						)
						.rotateLeftLeg(
								(float) Math.toRadians(-lerp(10, 35, factor) + revision),
								0, 0
						)
						.end();
				break;
			case Back:
				transformer.rotateAdditionallyHeadPitch(-revision);
				if (notInitialized) {
					frontLeg = player.getRandom().nextBoolean();
					notInitialized = false;
				}
				if (frontLeg) {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(lerp(10, 45, factor)),
									0, 0
							)
							.rotateLeftLeg(
									(float) Math.toRadians(-lerp(10, 35, factor)),
									0, 0
							)
							.end();
				} else {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(-lerp(10, 35, factor)),
									0, 0
							)
							.rotateLeftLeg(
									(float) Math.toRadians(lerp(10, 45, factor)),
									0, 0
							)
							.end();
				}
				break;
		}
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / 6f;
		if (phase > 1) {
			return;
		}
		float factor = factorFunc(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(
						(parkourability.getDodge().getDodgeDirection() == Dodge.DodgeDirection.Back ? -1 : 1)
								* lerp(0, 30, factor)
				)
				.end();
	}

	private float factorFunc(float phase) {
		if (phase < 0.8) {
			return 1 - 5 * (phase - 0.45f) * (phase - 0.45f);
		} else {
			return (phase - 1) * (phase - 1) * 9.3f;
		}
	}
}
