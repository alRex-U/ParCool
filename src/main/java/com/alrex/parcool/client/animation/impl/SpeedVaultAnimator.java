package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ViewportEvent;

import static com.alrex.parcool.utilities.MathUtil.lerp;

public class SpeedVaultAnimator extends Animator {

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= Vault.MAX_TICK;
	}

	private float getFactor(float tick) {
		float phase = tick / Vault.MAX_TICK;
		if (phase < 0.5) {
			return EasingFunctions.SinInOutBySquare(phase * 2);
		} else {
			return EasingFunctions.SinInOutBySquare(2 - phase * 2);
		}
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / Vault.MAX_TICK;
		float factor = getFactor(getTick() + rotator.getPartialTick());
		float forwardFactor = (float) Math.sin(phase * 2 * Math.PI) + 0.5f;
		float yFactor = new Easing(phase)
				.squareOut(0, 0.5f, 0, 1)
				.squareIn(0.5f, 1, 1, 0)
				.get();
		rotator
				.startBasedCenter()
				.translateY(-yFactor * player.getBbHeight() / 5)
				.rotateRollRightward(factor * 60 * (type == Type.Right ? -1 : 1))
				.rotatePitchFrontward(30 * forwardFactor)
				.end();
    }

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / Vault.MAX_TICK;
		float animFactor = new Easing(phase)
				.sinInOut(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.sinInOut(0.75f, 1, 1, 0)
				.get();
		float freeArmXFactor = new Easing(phase)
				.squareOut(0, 0.65f, 1, -1)
				.sinInOut(0.65f, 1, -1, 0)
				.get();
		float freeArmZFactor = new Easing(phase)
				.squareOut(0, 0.40f, 0, 1)
				.sinInOut(0.40f, 0.65f, 1, -0.3f)
				.sinInOut(0.65f, 1, -0.3f, 0)
				.get();
		float factor = getFactor(getTick() + transformer.getPartialTick());
		switch (type) {
			case Right:
				transformer
						.rotateLeftArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) -Math.toRadians(factor * 70)
						)
						.rotateRightArm(
								(float) Math.toRadians(20 + freeArmXFactor * 50),
								0,
								(float) Math.toRadians(freeArmZFactor * 40),
								animFactor
						)
						.addRotateRightLeg(0, 0, (float) Math.toRadians(factor * 30))
						.addRotateLeftLeg(0, 0, (float) Math.toRadians(factor * 20))
                        .rotateAdditionallyHeadRoll(factor * -15)
						.end();
				break;

			case Left:
				transformer
						.rotateRightArm(
								(float) Math.toRadians(lerp(-45, 45, phase)),
								0,
								(float) Math.toRadians(factor * 70)
						)
						.rotateLeftArm(
								(float) Math.toRadians(20 + freeArmXFactor * 50),
								0,
								(float) Math.toRadians(freeArmZFactor * -40),
								animFactor
						)
						.addRotateRightLeg(0, 0, (float) Math.toRadians(factor * -20))
						.addRotateLeftLeg(0, 0, (float) Math.toRadians(factor * -30))
                        .rotateAdditionallyHeadRoll(factor * 35)
						.end();
				break;
		}
	}

	@Override
	public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get()) return;
		float factor = getFactor((float) (getTick() + event.getPartialTick()));
		float phase = (float) ((getTick() + event.getPartialTick()) / Vault.MAX_TICK);
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

    private final Type type;

	public SpeedVaultAnimator(Type type) {
		this.type = type;
	}
}
