package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import static java.lang.Math.toRadians;

public class KongVaultAnimator extends Animator {
	private static final int MAX_TIME = 11;

	float getFactor(float phase) {
		if (phase < 0.5) {
			return EasingFunctions.SinInOutBySquare(phase * 2);
		} else {
			return EasingFunctions.SinInOutBySquare(2 - phase * 2);
		}
		//return 1 - 4 * MathUtil.squaring(phase - 0.5f);
	}

	float getArmFactor(float phase) {
		return phase < 0.2 ?
				1 - 25 * (phase - 0.2f) * (phase - 0.2f) :
				1 - EasingFunctions.SinInOutBySquare((phase - 0.2f) * 1.25f);
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return getTick() >= MAX_TIME;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MAX_TIME;
		float armFactor = getArmFactor(phase);
		float factor = getFactor(phase);
		float animFactor = new Easing(phase)
				.sinInOut(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.sinInOut(0.75f, 1, 1, 0)
				.get();
		transformer
				.rotateAdditionallyHeadPitch(-40 * armFactor)
				.rotateRightArm((float) toRadians(30 - 195 * armFactor), 0, (float) toRadians(30 - 30 * armFactor), animFactor)
				.rotateLeftArm((float) toRadians(25 - 195 * armFactor), 0, (float) toRadians(-30 + 30 * armFactor), animFactor)
				.rotateRightLeg((float) toRadians(-20 + 55 * factor), 0, 0, animFactor)
				.rotateLeftLeg((float) toRadians(-10 + 20 * factor), 0, 0, animFactor)
				.makeLegsLittleMoving()
				.end();
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_TIME;
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
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, PlayerEntity clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get()
		) return;
		float phase = (float) ((getTick() + event.getRenderPartialTicks()) / MAX_TIME);
		float factor = getFactor(phase);
		event.setPitch(30 * factor + clientPlayer.getViewXRot((float) event.getRenderPartialTicks()));
	}
}
