package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

import static java.lang.Math.toRadians;

public class WallJumpAnimator extends Animator {
	private boolean swingRightArm;
	private final int maxTick = 14;

	public WallJumpAnimator(boolean swingRightArm) {
		this.swingRightArm = swingRightArm;
	}

	float getFactor(float phase) {
		float x = phase - 0.2f;
		if (phase < 0.2) {
			return 1 - 25 * x * x;
		} else {
			return 1 - 1.5625f * x * x;
		}
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= maxTick;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / maxTick;
		float factor = getFactor(phase);
		int sign = swingRightArm ? 1 : -1;
		transformer
				.addRotateRightLeg((float) toRadians(sign * factor * (-12)), 0, 0)
				.addRotateLeftLeg((float) toRadians(sign * factor * 35), 0, 0)
				.rotateRightArm((float) toRadians(swingRightArm ? factor * (-120) : factor * 55), 0, 0)
				.rotateLeftArm((float) toRadians(swingRightArm ? factor * 55 : factor * (-120)), 0, 0)
				.makeArmsNatural()
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / maxTick;
		float factor = getFactor(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(factor * 30)
				.rotateRightward((swingRightArm ? -1 : 1) * 15 * factor)
				.end();
	}
}
