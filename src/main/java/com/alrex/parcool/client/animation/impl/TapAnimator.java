package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.entity.player.PlayerEntity;

public class TapAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= parkourability.get(Tap.class).getMaxTappingTick();
	}

	public float getAngleFactor(float phase) {
		return new Easing(phase)
				.squareOut(0, 0.25f, 0, 1)
				.sinInOut(0.25f, 1, 1, 0)
				.get();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / parkourability.get(Tap.class).getMaxTappingTick();
		if (phase > 1) phase = 1;
		float factor = getAngleFactor(phase);
		float animFactor = new Easing(phase)
				.squareOut(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.squareIn(0.75f, 1, 1, 0)
				.get();
		float angle = 80 * factor;
		transformer.getRawModel().rightLeg.z += factor;
		transformer.getRawModel().rightLeg.y -= 2 * factor;
		transformer.getRawModel().leftLeg.z -= 1.5f * factor;
		transformer.getRawModel().leftLeg.y -= 5f * factor;
		transformer.getRawModel().rightArm.y += 4f * factor;
		transformer
				.rotateLeftLeg(
						(float) Math.toRadians(-30 * factor), 0, 0, animFactor
				)
				.rotateRightLeg(
						(float) Math.toRadians(-15 * factor), 0, 0, animFactor
				)
				.rotateRightArm(
						(float) Math.toRadians(20 * factor), 0, (float) Math.toRadians(13 * factor), animFactor
				)
				.rotateLeftArm(
						(float) Math.toRadians(-angle), 0, 0, animFactor
				)
				.rotateAdditionallyHeadPitch(-40 * factor)
				.makeArmsNatural()
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / parkourability.get(Tap.class).getMaxTappingTick();
		if (phase > 1) phase = 1;
		float factor = getAngleFactor(phase);
		float angle = 80 * factor;
		rotator
				.startBasedCenter()
				.translateY(-factor * player.getBbHeight() / 5)
				.rotatePitchFrontward(angle)
				.endEnabledLegGrounding();
	}
}
