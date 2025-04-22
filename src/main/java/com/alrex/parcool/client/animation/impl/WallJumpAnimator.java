package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.entity.player.PlayerEntity;

import static java.lang.Math.toRadians;

public class WallJumpAnimator extends Animator {
	private final boolean wallRightSide;
	private final int maxTick = 11;

	public WallJumpAnimator(boolean wallIsRightSide) {
		this.wallRightSide = wallIsRightSide;
	}

	private float getFactor(float phase) {
		return Easing.with(phase)
				.squareOut(0, 0.3f, 0, 1)
				.linear(0.3f, 0.5f, 1, 1)
				.sinInOut(0.5f, 1, 1, 0)
				.get();
	}

	float getFadeFactor(float phase) {
		return (float) (1.0 - Math.pow(2 * phase - 1, 8));
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= maxTick;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / maxTick;
		float factor = getFactor(phase);
		float fadeFactor = getFadeFactor(phase);
		if (wallRightSide) {
			transformer
					.translateRightLeg(
							0,
							-1.1f * Easing.with(phase).squareOut(0, 0.15f, -1f, 1).sinInOut(0.15f, 1, 1, 0).get(),
							3.2f * Easing.with(phase).squareOut(0, 0.15f, -0.9f, 1).sinInOut(0.15f, 1, 1, 0).get()
					)
					.translateLeftLeg(
							-1.1f * factor,
							0,
							-2.3f * factor
					)
					.translateRightArm(factor, -1.9f * factor, -2.5f * factor)
					.translateLeftArm(factor, factor, 1.9f * factor)
					.rotateRightLeg((float) toRadians(75 * factor), 0, 0, fadeFactor)
					.rotateLeftLeg((float) toRadians(-70 * factor), 0, 0, fadeFactor)
					.rotateRightArm((float) toRadians(factor * (-120)), 0, (float) toRadians(-35 * factor), fadeFactor)
					.rotateLeftArm((float) toRadians(factor * 55), 0, (float) toRadians(-35 * factor), fadeFactor)
					.makeArmsNatural()
					.end();
		} else {
			transformer
					.translateLeftLeg(
							0,
							-1.1f * Easing.with(phase).squareOut(0, 0.15f, -1f, 1).sinInOut(0.15f, 1, 1, 0).get(),
							3.2f * Easing.with(phase).squareOut(0, 0.15f, -0.9f, 1).sinInOut(0.15f, 1, 1, 0).get()
					)
					.translateRightLeg(
							1.1f * factor,
							0,
							-2.3f * factor
					)
					.translateLeftArm(-factor, -1.9f * factor, -2.5f * factor)
					.translateRightArm(-factor, factor, 1.9f * factor)
					.rotateRightLeg((float) toRadians(-70 * factor), 0, 0, fadeFactor)
					.rotateLeftLeg((float) toRadians(75 * factor), 0, 0, fadeFactor)
					.rotateRightArm((float) toRadians(factor * 55), 0, (float) toRadians(35 * factor), fadeFactor)
					.rotateLeftArm((float) toRadians(factor * (-120)), 0, (float) toRadians(35 * factor), fadeFactor)
					.makeArmsNatural()
					.end();
		}
	}

	@Override
    public void rotatePost(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / maxTick;
		float factor = getFactor(phase);
		rotator
				.startBasedCenter()
				.rotatePitchFrontward(factor * 20)
				.rotateRollRightward((wallRightSide ? -1 : 1) * 15 * factor)
				.end();
    }
}
