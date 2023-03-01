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

import static java.lang.Math.toRadians;

;

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
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= MAX_TIME;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / MAX_TIME;
		float armFactor = getArmFactor(phase);
		float factor = getFactor(phase);
		float fadeFactor = (float) (1 - Math.abs(Math.pow(2 * (phase - 0.5), 3)));
		transformer
				.rotateAdditionallyHeadPitch(-40 * armFactor)
				.rotateRightArm((float) toRadians(30 - 195 * armFactor), 0, (float) toRadians(30 - 30 * armFactor), fadeFactor)
				.rotateLeftArm((float) toRadians(25 - 195 * armFactor), 0, (float) toRadians(-30 + 30 * armFactor), fadeFactor)
				.rotateRightLeg((float) toRadians(-20 + 55 * factor), 0, 0, fadeFactor)
				.rotateLeftLeg((float) toRadians(-10 + 20 * factor), 0, 0, fadeFactor)
				.makeLegsLittleMoving()
				.end();
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / MAX_TIME;
		float factor = getFactor(phase);
		rotator
				.startBasedCenter()
				.rotateFrontward(factor * 95)
				.end();
	}

	@Override
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, Player clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				ParCoolConfig.CONFIG_CLIENT.disableCameraVault.get()) return;
		float phase = (float) ((getTick() + event.getPartialTicks()) / MAX_TIME);
		float factor = getFactor(phase);
		event.setPitch(30 * factor + clientPlayer.getViewXRot((float) event.getPartialTicks()));
	}
}
