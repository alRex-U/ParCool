package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class HorizontalWallRunAnimator extends Animator {
	final boolean wallIsRightSide;

	public HorizontalWallRunAnimator(boolean wallIsRightSide) {
		this.wallIsRightSide = wallIsRightSide;
	}

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(HorizontalWallRun.class).isDoing();
	}

	private float limbSwing = 0;
	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		limbSwing = transformer.getLimbSwing();
		float factor = getFactor(getTick() + transformer.getPartialTick());
		float angle = factor * 15f * (wallIsRightSide ? -1f : 1f);
		float armSwingPhase = limbSwing * 0.6662f;
		transformer
				.rotateAdditionallyHeadPitch(-15 * factor)
				.rotateAdditionallyHeadRoll(angle);
		if (wallIsRightSide) {
			transformer
					.addRotateLeftArm(0, 0, (float) Math.toRadians(-30))
					.makeArmsNatural()
					.rotateRightArm(
							(float) Math.toRadians(20 - 8d * Math.cos(armSwingPhase)),
							0,
							(float) Math.toRadians(110)
					)
					.translateRightArm(-1f, 0, 0.8f - 0.5f * Mth.cos(armSwingPhase))
					.addRotateLeftArm(
							(float) Math.toRadians(-10), 0,
							(float) -Math.toRadians(35 + 5 * Math.sin(armSwingPhase)), factor
					)
					.translateLeftArm(0, 1.0f, 0)
					.addRotateRightLeg(0, 0, (float) Math.toRadians(17))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(25))
					.rotateAdditionallyHeadYaw(-5f + 8f * Mth.cos(armSwingPhase))
					.end();
		} else {
			transformer
					.addRotateRightArm(0, 0, (float) Math.toRadians(30))
					.makeArmsNatural()
					.rotateLeftArm(
							(float) Math.toRadians(20 - 8d * Math.cos(armSwingPhase)),
							0,
							(float) Math.toRadians(-110)
					)
					.translateLeftArm(1f, 0, 0.8f - 0.5f * Mth.cos(armSwingPhase))
					.addRotateRightArm(
							(float) Math.toRadians(-10), 0,
							(float) Math.toRadians(35 + 5 * Math.sin(armSwingPhase)), factor
					)
					.translateRightArm(0, 1.0f, 0)
					.addRotateRightLeg(0, 0, (float) Math.toRadians(-25))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(-17))
					.rotateAdditionallyHeadYaw(5f - 8f * Mth.cos(armSwingPhase))
					.end();
		}
	}

	private float getFactor(float tick) {
		return tick < 5 ? 1 - MathUtil.squaring((5 - tick) / 5) : 1;
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float factor = getFactor(getTick() + rotator.getPartialTick());
		float sign = wallIsRightSide ? -1 : 1;
		float angle = factor * 30f * sign;
		float yOffset = 0.145f * (float) Math.pow(Math.cos(limbSwing * 0.6662), 2.);
		rotator.translateY(yOffset)
				.startBasedCenter()
				.rotateRollRightward(angle)
				.rotatePitchFrontward(20 * factor)
				.rotateYawRightward(sign * (-5f + 8f * Mth.cos(limbSwing * 0.66662f)))
				.end();
    }

	@Override
	public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfHWallRun.get()
		)
			return;
		float factor = getFactor((float) (getTick() + event.getPartialTick()));
		float angle = factor * 20 * (wallIsRightSide ? -1 : 1);
		event.setRoll(angle);
	}
}
