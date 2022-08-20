package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

import static com.alrex.parcool.utilities.MathUtil.lerp;
import static com.alrex.parcool.utilities.MathUtil.squaring;

public class SpeedVaultAnimator extends Animator {
	private static final int MAX_TIME = 11;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= MAX_TIME;
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_TIME;
		float factor = -squaring(((getTick() + rotator.getPartialTick()) - MAX_TIME / 2f) / (MAX_TIME / 2f)) + 1;
		float forwardFactor = (float) Math.sin(phase * 2 * Math.PI) + 0.5f;

		rotator
				.startBasedCenter()
				.rotateRightward(factor * 70 * (type == Type.Right ? -1 : 1))
				.rotateFrontward(30 * forwardFactor)
				.end();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MAX_TIME;
		float factor = -squaring(((getTick() + transformer.getPartialTick()) - MAX_TIME / 2f) / (MAX_TIME / 2f)) + 1;
		switch (type) {
			case Right:
				transformer
						.rotateLeftArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) -Math.toRadians(factor * 70)
						)
						.end();
				break;

			case Left:
				transformer
						.rotateRightArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) Math.toRadians(factor * 70)
						)
						.end();
				break;
		}
	}

	public enum Type {Right, Left}

	private Type type;

	public SpeedVaultAnimator(Type type) {
		this.type = type;
	}
}
