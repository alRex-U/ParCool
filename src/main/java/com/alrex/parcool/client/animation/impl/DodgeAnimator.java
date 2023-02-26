package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

import static com.alrex.parcool.utilities.MathUtil.lerp;

;

public class DodgeAnimator extends Animator {
	private final static Random rand = new Random();
	private boolean notInitialized = true;
	private boolean frontLeg = false;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Dodge.class).isDoing();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / 6f;
		if (phase > 1) {
			return;
		}
		float fadeFactor = 1 - phase * phase * phase * phase;
		float factor = factorFunc(phase);
		float revision = -lerp(0, 30, factor);
		switch (parkourability.get(Dodge.class).getDodgeDirection()) {
			case Front:
				transformer.rotateAdditionallyHeadPitch(revision);
				if (notInitialized) {
					frontLeg = rand.nextBoolean();
					notInitialized = false;
				}
				if (frontLeg) {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(-lerp(10, 35, factor) + revision),
									0, 0, fadeFactor
							)
							.rotateLeftLeg(
									(float) Math.toRadians(lerp(30, 60, factor) + revision),
									0, 0, fadeFactor
							)
							.end();
				} else {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(lerp(30, 60, factor) + revision),
									0, 0, fadeFactor
							)
							.rotateLeftLeg(
									(float) Math.toRadians(-lerp(10, 35, factor) + revision),
									0, 0, fadeFactor
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
								0, 0, fadeFactor
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
								0, 0, fadeFactor
						)
						.end();
				break;
			case Back:
				transformer.rotateAdditionallyHeadPitch(-revision);
				if (notInitialized) {
					frontLeg = rand.nextBoolean();
					notInitialized = false;
				}
				if (frontLeg) {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(lerp(10, 45, factor)),
									0, 0, fadeFactor
							)
							.rotateLeftLeg(
									(float) Math.toRadians(-lerp(10, 35, factor)),
									0, 0, fadeFactor
							)
							.end();
				} else {
					transformer
							.rotateRightLeg(
									(float) Math.toRadians(-lerp(10, 35, factor)),
									0, 0, fadeFactor
							)
							.rotateLeftLeg(
									(float) Math.toRadians(lerp(10, 45, factor)),
									0, 0, fadeFactor
							)
							.end();
				}
				break;
		}
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / 6f;
		if (phase > 1) {
			return;
		}
		float factor = factorFunc(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(
						(parkourability.get(Dodge.class).getDodgeDirection() == Dodge.DodgeDirection.Back ? -1 : 1)
								* lerp(0, 30, factor)
				)
				.end();
	}

	private float factorFunc(float phase) {
		if (phase < 0.5) {
			return 1 - 4 * MathUtil.squaring(phase - 0.5f);
		} else {
			return 1 - EasingFunctions.SinInOutBySquare(2 * (phase - 0.5f));
		}
	}
}
