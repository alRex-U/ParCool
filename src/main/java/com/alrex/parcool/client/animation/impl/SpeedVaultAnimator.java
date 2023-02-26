package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;

;

public class SpeedVaultAnimator extends Animator {
	private static final int MAX_TIME = 11;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= MAX_TIME;
	}

	private float getFactor(float tick) {
		float phase = tick / MAX_TIME;
		if (phase < 0.5) {
			return EasingFunctions.SinInOutBySquare(phase * 2);
		} else {
			return EasingFunctions.SinInOutBySquare(2 - phase * 2);
		}
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_TIME;
		float factor = getFactor(getTick() + rotator.getPartialTick());
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
		float factor = getFactor(getTick() + transformer.getPartialTick());
		switch (type) {
			case Right:
				transformer
						.rotateLeftArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) -Math.toRadians(factor * 70)
						)
						.addRotateRightLeg(0, 0, (float) Math.toRadians(factor * 25))
						.addRotateLeftLeg(0, 0, (float) Math.toRadians(factor * 15))
						.end();
				break;

			case Left:
				transformer
						.rotateRightArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) Math.toRadians(factor * 70)
						)
						.addRotateRightLeg(0, 0, (float) Math.toRadians(factor * -15))
						.addRotateLeftLeg(0, 0, (float) Math.toRadians(factor * -25))
						.end();
				break;
		}
	}

	@Override
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, Player clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				ParCoolConfig.CONFIG_CLIENT.disableCameraVault.get()) return;
		float factor = getFactor((float) (getTick() + event.getPartialTicks()));
		float phase = (float) ((getTick() + event.getPartialTicks()) / MAX_TIME);
		float forwardFactor = (float) Math.sin(phase * 2 * Math.PI) + 0.5f;
		event.setPitch(15 * forwardFactor);
		switch (type) {
			case Right:
				event.setRoll(-25 * factor);
				break;
			case Left:
				event.setRoll(25 * factor);
				break;
		}
	}

	public enum Type {Right, Left}

	private Type type;

	public SpeedVaultAnimator(Type type) {
		this.type = type;
	}
}
