package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.world.entity.player.Player;

import static com.alrex.parcool.utilities.MathUtil.lerp;

;

public class CatLeapAnimator extends Animator {

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(CatLeap.class).isDoing() || getTick() > 20;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		CatLeap catLeap = parkourability.get(CatLeap.class);

		float phase = (catLeap.getDoingTick() + transformer.getPartialTick()) / 20f;
		if (phase > 1) phase = 1f;
		float factor = movingFactorFunc(phase);
		float animationFactor = 1 - phase * phase * phase * phase;
		transformer.getRawModel().leftLeg.z -=
				Easing.with(phase)
						.squareOut(0, 0.1f, 0f, -1.2f)
						.sinInOut(0.1f, 0.25f, -1.2f, 0.6f)
						.sinInOut(0.25f, 1, 0.6f, 0f)
						.get();
		transformer.getRawModel().rightLeg.z +=
				Easing.with(phase)
						.squareOut(0, 0.1f, 0f, -1.2f)
						.sinInOut(0.1f, 0.25f, -1.2f, 0.6f)
						.sinInOut(0.25f, 1, 0.6f, 0f)
						.get();
		float armY = 1.2f * factor * animationFactor;
		float armZ = 0.8f * factor * animationFactor;
		transformer.getRawModel().leftArm.y -= armY;
		transformer.getRawModel().leftArm.z -= armZ;
		transformer.getRawModel().rightArm.y -= armY;
		transformer.getRawModel().rightArm.z -= armZ;

		transformer
				.rotateLeftArm(
						(float) -Math.toRadians(lerp(-25f, 170f, factor)),
						0,
						(float) -Math.toRadians(lerp(24, -4, factor)),
						animationFactor
				)
				.rotateRightArm(
						(float) -Math.toRadians(lerp(-25f, 170f, factor)),
						0,
						(float) Math.toRadians(lerp(24, -4, factor)),
						animationFactor
				)
				.makeArmsNatural()
				.rotateLeftLeg(
						(float) Math.toRadians(-15 + Easing.with(phase)
								.squareOut(0, 0.1f, -5f, 65f)
								.sinInOut(0.1f, 0.25f, 65f, -40f)
								.sinInOut(0.25f, 0.65f, -40f, 20f)
								.sinInOut(0.65f, 1f, 20f, 0f)
								.get()
						),
						0,
						0,
						animationFactor
				)
				.rotateRightLeg(
						(float) Math.toRadians(-15 + Easing.with(phase)
								.squareOut(0, 0.1f, 10f, -40f)
								.sinInOut(0.1f, 0.25f, -40f, 65f)
								.sinInOut(0.25f, 0.65f, 65f, -15f)
								.sinInOut(0.65f, 1, -15f, 0f)
								.get()
						),
						0,
						0,
						animationFactor
				)
				.end();
	}

	private float movingFactorFunc(float phase) {
		if (phase > 0.2) {
			return 1 - EasingFunctions.CubicInOut((phase - 0.2f) * 1.25f);
		} else {
			return (float) (1 - 25 * (phase - 0.2) * (phase - 0.2));
		}
	}
}
