package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent;

public class HorizontalWallRunAnimator extends Animator {
	final boolean wallIsRightSide;

	public HorizontalWallRunAnimator(boolean wallIsRightSide) {
		this.wallIsRightSide = wallIsRightSide;
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(HorizontalWallRun.class).isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		if (wallIsRightSide) {
			transformer
					.addRotateLeftArm(0, 0, (float) Math.toRadians(-30))
					.makeArmsNatural()
					.rotateRightArm(0, 0, (float) Math.toRadians(60))
					.addRotateRightLeg(0, 0, (float) Math.toRadians(17))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(25))
					.end();
		} else {
			transformer
					.addRotateRightArm(0, 0, (float) Math.toRadians(30))
					.makeArmsNatural()
					.rotateLeftArm(0, 0, (float) Math.toRadians(-60))
					.addRotateRightLeg(0, 0, (float) Math.toRadians(-25))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(-17))
					.end();
		}
	}

	private float getFactor(float tick) {
		return tick < 5 ? 1 - MathUtil.squaring((5 - tick) / 5) : 1;
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		float factor = getFactor(getTick() + rotator.getPartialTick());
		float angle = factor * 30 * (wallIsRightSide ? -1 : 1);
		rotator
				.startBasedCenter()
				.rotateRollRightward(angle)
				.end();
	}

	@Override
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, PlayerEntity clientPlayer, Parkourability parkourability) {
		if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
				!ParCoolConfig.Client.Booleans.EnableCameraAnimationOfHWallRun.get()
		)
			return;
		float factor = getFactor((float) (getTick() + event.getRenderPartialTicks()));
		float angle = factor * 20 * (wallIsRightSide ? -1 : 1);
		event.setRoll(angle);
	}
}
