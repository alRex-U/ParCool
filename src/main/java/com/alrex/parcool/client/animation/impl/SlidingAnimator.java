package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class SlidingAnimator extends Animator {
	private static final int MAX_TRANSITION_TICK = 5;
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Slide.class).isDoing();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float animFactor = (getTick() + transformer.getPartialTick()) / MAX_TRANSITION_TICK;
		if (animFactor > 1) animFactor = 1;
		animFactor = new Easing(animFactor)
				.sinInOut(0, 1, 0, 1)
				.get();

		transformer
				.rotateHeadPitch(50)
				.rotateRightArm((float) Math.toRadians(45), 0, (float) Math.toRadians(110), animFactor)
				.rotateLeftArm((float) Math.toRadians(50), 0, (float) Math.toRadians(-100), animFactor)
				.rotateRightLeg((float) Math.toRadians(-17), 0, (float) Math.toRadians(-5), animFactor)
				.rotateLeftLeg((float) Math.toRadians(-5), 0, (float) Math.toRadians(15), animFactor)
				.makeLegsLittleMoving()
				.makeArmsNatural()
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float swimAmount = player.getSwimAmount(rotator.getPartialTick());
		float bodyAnglePhase = (getTick() + rotator.getPartialTick()) / MAX_TRANSITION_TICK;
		if (bodyAnglePhase > 1) bodyAnglePhase = 1;
		float bodyAngleFactor = new Easing(bodyAnglePhase)
				.squareOut(0, 1, 0, 1)
				.get();
		rotator
				.startBasedCenter()
				.translateY(player.getBbHeight() / 4 * (1 - bodyAnglePhase))
				.rotatePitchFrontward(-70 * bodyAngleFactor - 90 * swimAmount)
				.end();
	}
}
