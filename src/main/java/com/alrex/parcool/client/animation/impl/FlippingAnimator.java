package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ViewportEvent;

public class FlippingAnimator extends Animator {
    public FlippingAnimator(Flipping.Direction direction) {
		this.direction = direction;
	}

	private final Flipping.Direction direction;

	private int getMaxAnimationTick() {
		return 12;
	}

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Flipping.class).isDoing() || getTick() >= getMaxAnimationTick();
	}

	private float angleFactor(float phase) {
		return EasingFunctions.SinInOutBySquare(phase);
	}

	private float armAngleXFactorFront(float phase) {
		if (phase < 0.1) {
			return 1 - 100 * (phase - 0.1f) * (phase - 0.1f);
		} else if (phase < 0.6) {
			return 1 - 0.8f * EasingFunctions.CubicInOut((phase - 0.1f) * 2);
		} else {
			return -0.5f * phase + 0.5f;
		}
	}

	private float armAngleXFactorBack(float phase) {
		if (phase < 0.1) {
			return 1 - 100 * (phase - 0.1f) * (phase - 0.1f);
		} else if (phase < 0.6) {
			return 1 - EasingFunctions.SinInOutBySquare((phase - 0.1f) * 2);
		} else {
			return 0.1f * EasingFunctions.SinInOutBySquare((phase - 0.6f) * 2.5f);
		}
	}

	private float legAngleFactorBack(float phase) {
		if (phase < 0.5) {
			return 8 * (phase - 0.25f) * (phase - 0.25f);
		} else {
			return 1 - 8 * (phase - 0.75f) * (phase - 0.75f);
		}
	}

	private float armAngleZFactor(float phase) {
		if (phase < 0.5) {
			return phase * 0.4f;
		} else if (phase < 0.75) {
			return 0.2f + 0.8f * EasingFunctions.CubicInOut((phase - 0.5f) * 4);
		} else {
			return 1 - EasingFunctions.CubicInOut((phase - 0.75f) * 4);
		}
	}

	private float legAngleFactorFront(float phase) {
		return 1 - 4 * (phase - 0.5f) * (phase - 0.5f);
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / getMaxAnimationTick();

        if (direction == Flipping.Direction.Front) {
			float armAngleX = -180 * armAngleXFactorFront(phase) + 25;
			float armAngleZ = MathUtil.lerp(phase > 0.75 ? 0 : 14, 28, armAngleZFactor(phase));
			float legAngleX = MathUtil.lerp(0, -50, legAngleFactorFront(phase));
			float headAngle = MathUtil.lerp(0, 45, legAngleFactorFront(phase));
			transformer
					.rotateAdditionallyHeadPitch(
							headAngle
					)
					.rotateRightArm(
							(float) Math.toRadians(armAngleX),
							0,
							(float) Math.toRadians(armAngleZ)
					)
					.rotateLeftArm(
							(float) Math.toRadians(armAngleX),
							0,
							(float) -Math.toRadians(armAngleZ)
					)
					.makeArmsNatural()
					.rotateRightLeg(
							(float) Math.toRadians(legAngleX - 15),
							0, 0
					)
					.rotateLeftLeg(
							(float) Math.toRadians(legAngleX + 15),
							0, 0
					)
					.makeLegsLittleMoving()
					.end();
		} else {
			float armAngleX = MathUtil.lerp(20, -190, armAngleXFactorBack(phase));
			float armAngleZ = MathUtil.lerp(phase > 0.75 ? 0 : 14, 28, armAngleZFactor(phase));
			float legAngle = MathUtil.lerp(35, -35, legAngleFactorBack(phase));
			float headAngle = MathUtil.lerp(0, -45, 1 - 4 * (phase - 0.5f) * (phase - 0.5f));
			transformer
					.rotateAdditionallyHeadPitch(
							headAngle
					)
					.rotateRightArm(
							(float) Math.toRadians(armAngleX),
							0,
							(float) Math.toRadians(armAngleZ)
					)
					.rotateLeftArm(
							(float) Math.toRadians(armAngleX),
							0,
							(float) -Math.toRadians(armAngleZ)
					)
					.makeArmsNatural()
					.rotateRightLeg(
							(float) Math.toRadians(legAngle - 15),
							0, 0
					)
					.rotateLeftLeg(
							(float) Math.toRadians(legAngle + 15),
							0, 0
					)
					.makeLegsLittleMoving()
					.end();
		}
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / getMaxAnimationTick();
		float factor = angleFactor(phase);

		float angle;
        if (direction == Flipping.Direction.Front) {
			angle = factor * 360;
		} else {
			angle = factor * -360;
		}
		rotator
				.startBasedCenter()
				.rotatePitchFrontward(angle)
				.end();
    }

	@Override
	public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		if (!clientPlayer.isLocalPlayer() ||
				!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfFlipping.get()
		) return;
		float phase = (float) ((getTick() + event.getPartialTick()) / getMaxAnimationTick());
		float factor = angleFactor(phase);
        if (direction == Flipping.Direction.Front) {
			event.setPitch(clientPlayer.getViewXRot((float) event.getPartialTick()) + factor * 360 - ((phase > 0.5) ? 360 : 0));
		} else {
			event.setPitch(clientPlayer.getViewXRot((float) event.getPartialTick()) - factor * 360 + ((phase > 0.5) ? 360 : 0));
		}
	}
}
