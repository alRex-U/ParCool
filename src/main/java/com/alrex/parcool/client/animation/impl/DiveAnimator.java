package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class DiveAnimator extends Animator {
	public DiveAnimator(double startYSpeed) {
		this.startYSpeed = startYSpeed;
	}

	private final double startYSpeed;
	private float oldFactor = 0;

	private float getFactor(double yMovement) {
		return (float) (-2 * Math.atan((yMovement - startYSpeed) / startYSpeed) / Math.PI);
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(Dive.class).isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		double ySpeed = player.isLocalPlayer() ? player.getDeltaMovement().y() : parkourability.get(Dive.class).getPlayerYSpeed();
		float factor = getFactor(ySpeed);
		transformer
				.rotateHeadPitch(-50 * factor)
				.rotateRightArm(0, 0, (float) Math.toRadians(195 * factor))
				.rotateLeftArm(0, 0, (float) Math.toRadians(-195 * factor))
				.makeArmsNatural()
				.rotateRightLeg((float) Math.toRadians(-180 * (factor - oldFactor)), 0, 0)
				.rotateLeftLeg((float) Math.toRadians(-180 * (factor - oldFactor)), 0, 0);
		oldFactor = factor;
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		double ySpeed = player.isLocalPlayer() ? player.getDeltaMovement().y() : parkourability.get(Dive.class).getPlayerYSpeed();
		float factor = getFactor(ySpeed);
		rotator.startBasedCenter()
				.rotateFrontward(180 * factor)
				.end();
	}
}
