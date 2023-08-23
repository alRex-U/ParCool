package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.entity.player.PlayerEntity;

public class ClimbUpAnimator extends Animator {
	private static final int MaxTick = 10;

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return MaxTick <= getTick();
	}

	private float bodyAngleFactor(float phase) {
		if (phase <= 0.3) {
			return 1 - 11.1f * (phase - 0.3f) * (phase - 0.3f);
		} else {
			return 1 - EasingFunctions.SinInOutBySquare((phase - 0.3f) * 1.45f);
		}
	}

	private float armAngle(float phase) {
		if (phase <= 0.3) {
			return MathUtil.lerp(0, -170, 11.1f * (phase - 0.3f) * (phase - 0.3f));
		} else {
			return 0;
		}
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MaxTick;
		float factor = bodyAngleFactor(phase);

		rotator
				.startBasedCenter()
				.rotatePitchFrontward(factor * 50)
				.end();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MaxTick;
		float angleArm = armAngle(phase);
		float bodyAngleFactor = bodyAngleFactor(phase);
		transformer
				.rotateRightArm((float) Math.toRadians(angleArm), 0, 0)
				.rotateLeftArm((float) Math.toRadians(angleArm), 0, 0)
				.addRotateLeftLeg((float) Math.toRadians(bodyAngleFactor * -50), 0, 0)
				.addRotateRightLeg((float) Math.toRadians(bodyAngleFactor * -50), 0, 0)
				.makeLegsLittleMoving()
				.makeArmsNatural()
				.end();
	}
}
