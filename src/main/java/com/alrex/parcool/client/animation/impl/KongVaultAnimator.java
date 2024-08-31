package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ViewportEvent;

import static java.lang.Math.toRadians;

public class KongVaultAnimator extends Animator {

	float getFactor(float phase) {
		if (phase < 0.5) {
			return EasingFunctions.SinInOutBySquare(phase * 2);
		} else {
			return EasingFunctions.SinInOutBySquare(2 - phase * 2);
		}
	}

	float getArmFactor(float phase) {
		return phase < 0.2 ?
				1 - 25 * (phase - 0.2f) * (phase - 0.2f) :
				1 - EasingFunctions.SinInOutBySquare((phase - 0.2f) * 1.25f);
	}

	private float yRotDifference = 0;
	private float yRotDifferenceOld = 0;

	@Override
	public void tick(Player player) {
		super.tick(player);
		yRotDifferenceOld = yRotDifference;
		Vec3 currentAngle = VectorUtil.fromYawDegree(player.yBodyRot);
		Vec3 oldAngle = VectorUtil.fromYawDegree(player.yBodyRotO);
		yRotDifference = (float) Math.atan(
				(oldAngle.x() * currentAngle.z() - currentAngle.x() * oldAngle.z())
						/ (currentAngle.x() * oldAngle.x() + currentAngle.z() * oldAngle.z())
		);
	}

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= Vault.MAX_TICK;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / Vault.MAX_TICK;
		float armFactor = getArmFactor(phase);
		float factor = getFactor(phase);
		float animFactor = new Easing(phase)
				.sinInOut(0, 0.15f, 0, 1)
				.linear(0.15f, 0.85f, 1, 1)
				.sinInOut(0.85f, 1, 1, 0)
				.get();
		float difference = MathUtil.lerp(yRotDifferenceOld, yRotDifference, transformer.getPartialTick());
		transformer
                .translateLeftLeg(
                        0,
                        -0.7f * factor,
                        -0.9f * factor
                )
                .translateRightLeg(
                        0,
                        -0.7f * factor,
                        -0.9f * factor
                )
				.rotateAdditionallyHeadPitch(-40 * armFactor)
				.rotateRightArm((float) toRadians(30 - 195 * armFactor), 0, (float) toRadians(30 - 30 * armFactor), animFactor)
				.rotateLeftArm((float) toRadians(25 - 195 * armFactor), 0, (float) toRadians(-30 + 30 * armFactor), animFactor)
				.rotateRightLeg(
						(float) toRadians(Easing.with(phase)
								.squareOut(0, 0.1f, 5, -5)
								.sinInOut(0.1f, 0.47f, -5, 25)
								.sinInOut(0.47f, 0.9f, 25, -25)
								.sinInOut(0.9f, 1f, -25, 0)
								.get()
						),
						0,
						difference - (float) Math.toRadians(5. * factor),
						animFactor
				)
				.rotateLeftLeg(
						(float) toRadians(Easing.with(phase)
								.sinInOut(0, 0.33f, -20, 20)
								.sinInOut(0.33f, 0.79f, 20, -45)
								.sinInOut(0.79f, 1f, -45, 0)
								.get()
						),
						0,
						difference + (float) Math.toRadians(5. * factor),
						animFactor)
				.makeLegsLittleMoving()
				.end();
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / Vault.MAX_TICK;
		float factor = getFactor(phase);
		float yFactor = new Easing(phase)
				.squareOut(0, 0.5f, 0, 1)
				.squareIn(0.5f, 1, 1, 0)
				.get();
		rotator
				.startBasedCenter()
				.translateY(-yFactor * player.getBbHeight() / 5)
				.rotatePitchFrontward(factor * 95)
				.end();
    }

	@Override
	public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get()
		) return;
		float phase = (float) ((getTick() + event.getPartialTick()) / Vault.MAX_TICK);
		float factor = getFactor(phase);
		event.setPitch(30 * factor + clientPlayer.getViewXRot((float) event.getPartialTick()));
	}
}
