package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.capability.Parkourability;
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
	public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		return false;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		CatLeap catLeap = parkourability.get(CatLeap.class);

		float phase = (catLeap.getDoingTick() + transformer.getPartialTick()) / 20f;
		if (phase > 1) phase = 1f;
		float factor = movingFactorFunc(phase);
		float animationFactor = 1 - phase * phase * phase * phase;
		transformer
				.rotateLeftArm(
						(float) -Math.toRadians(lerp(-25f, 170f, factor)),
						0,
						(float) -Math.toRadians(lerp(24, 5, factor)),
						animationFactor
				)
				.rotateRightArm(
						(float) -Math.toRadians(lerp(-25f, 170f, factor)),
						0,
						(float) Math.toRadians(lerp(24, 5, factor)),
						animationFactor
				)
				.makeArmsNatural()
				.rotateLeftLeg(
						(float) Math.toRadians(lerp(15f, 45f, factor)),
						0,
						0,
						animationFactor
				)
				.rotateRightLeg(
						(float) -Math.toRadians(lerp(15f, 45f, factor)),
						0,
						0,
						animationFactor
				)
				.makeLegsLittleMoving()
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
