package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.entity.player.PlayerEntity;

public class VerticalWallRunAnimator extends Animator {
	static final int MAX_ANIMATION_TICK = 10;
	static final int ROLL_ANGLE = 15;

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= MAX_ANIMATION_TICK;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MAX_ANIMATION_TICK;
		if (phase > 1) {
			return;
		}
		float animFactor = new Easing(phase)
				.linear(0.0f, 0.75f, 1, 1)
				.squareIn(0.75f, 1, 1, 0)
				.get();
		float rightLegXFactor = new Easing(phase)
				.squareOut(0, 0.25f, 0, -1)
				.sinInOut(0.25f, 0.55f, -1, 1)
				.sinInOut(0.55f, 1, 1, 0)
				.get();
		float leftLegXFactor = new Easing(phase)
				.squareOut(0, 0.25f, 0, 1)
				.sinInOut(0.25f, 0.55f, 1, -1)
				.sinInOut(0.55f, 1, -1, 0)
				.get();
		float rightArmXFactor = new Easing(phase)
				.squareOut(0, 0.55f, 0, 1)
				.sinInOut(0.55f, 1, 1, 0)
				.get();
		float rightArmZFactor = new Easing(phase)
				.sinInOut(0, 0.3f, 0, 1)
				.sinInOut(0.3f, 0.6f, 1, 0.1f)
				.linear(0.6f, 1, 0.1f, 0)
				.get();
		float leftArmXFactor = new Easing(phase)
				.sinInOut(0f, 0.65f, 1, -0.3f)
				.sinInOut(0.65f, 1, -0.3f, 0)
				.get();
		float leftArmZFactor = new Easing(phase)
				.linear(0, 0.25f, 0, 0)
				.sinInOut(0.25f, 0.6f, 0, 1)
				.sinInOut(0.6f, 1, 1, 0)
				.get();
		float headFactor = new Easing(phase)
				.sinInOut(0, 0.3f, 0, 1)
				.sinInOut(0.3f, 1, 1, 0)
				.get();
		float rollFactor = new Easing(phase)
				.squareOut(0, 0.4f, 0, 1)
				.linear(0.4f, 0.75f, 1, 1)
				.sinInOut(0.75f, 1, 1, 0)
				.get();
		transformer
				.rotateAdditionallyHeadPitch(15 * headFactor)
				.rotateAdditionallyHeadRoll(-ROLL_ANGLE * rollFactor / 2)
				.rotateRightLeg((float) Math.toRadians(-45 + 50 * rightLegXFactor), 0, (float) Math.toRadians(-ROLL_ANGLE * 0.95 * rollFactor), animFactor)
				.rotateLeftLeg((float) Math.toRadians(-40 + 55 * leftLegXFactor), 0, (float) Math.toRadians(-ROLL_ANGLE * 1.2 * rollFactor), animFactor)
				.rotateRightArm((float) Math.toRadians(-180 * rightArmXFactor), 0, (float) Math.toRadians(-ROLL_ANGLE * rollFactor - 20 * rightArmZFactor), animFactor)
				.rotateLeftArm((float) Math.toRadians(-180 * leftArmXFactor), 0, (float) Math.toRadians(leftArmZFactor * -30), animFactor)
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_ANIMATION_TICK;
		if (phase > 1) {
			return;
		}
		float factor = new Easing(phase)
				.sinInOut(0, 0.3f, 0, 1)
				.sinInOut(0.3f, 1, 1, 0)
				.get();
		float rollFactor = new Easing(phase)
				.squareOut(0, 0.4f, 0, 1)
				.linear(0.4f, 0.75f, 1, 1)
				.sinInOut(0.75f, 1, 1, 0)
				.get();
		rotator.startBasedCenter()
				.rotatePitchFrontward(-10 * factor)
				.rotateRollRightward(-ROLL_ANGLE * rollFactor)
				.end();
	}
}
