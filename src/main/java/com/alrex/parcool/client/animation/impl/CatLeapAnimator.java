package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.world.entity.player.Player;

import static com.alrex.parcool.utilities.MathUtil.lerp;

public class CatLeapAnimator extends Animator {

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.getCatLeap().isLeaping() || getTick() > 30;
	}

	@Override
	public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		return false;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		CatLeap catLeap = parkourability.getCatLeap();

		float phase = (catLeap.getLeapingTick() + transformer.getPartialTick()) / 30f;
		if (phase > 1) phase = 1f;
		float factor = movingFactorFunc(phase);
		transformer
				.rotateLeftArm(
						(float) -Math.toRadians(lerp(20f, 170f, factor)),
						0,
						(float) -Math.toRadians(lerp(10, 0, factor))
				)
				.rotateRightArm(
						(float) -Math.toRadians(lerp(20f, 170f, factor)),
						0,
						(float) Math.toRadians(lerp(10, 0, factor))
				)
				.makeArmsNatural()
				.rotateLeftLeg(
						(float) Math.toRadians(lerp(15f, 45f, factor)),
						0,
						0
				)
				.rotateRightLeg(
						(float) -Math.toRadians(lerp(15f, 45f, factor)),
						0,
						0
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
