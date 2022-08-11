package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.entity.player.PlayerEntity;

public class RollAnimator extends Animator {
	public static float calculateMovementFactor(float progress) {
		return -MathUtil.squaring(progress - 1) + 1;
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getRoll().isRolling();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {

	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		Roll roll = parkourability.getRoll();
		float factor = calculateMovementFactor((roll.getRollingTick() + rotator.getPartial()) / (float) roll.getRollMaxTick());
		rotator
				.startBasedCenter()
				.rotateFrontward(MathUtil.lerp(0, 360, factor))
				.End();
	}
}
