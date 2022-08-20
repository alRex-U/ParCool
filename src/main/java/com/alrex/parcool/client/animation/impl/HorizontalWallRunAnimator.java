package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

public class HorizontalWallRunAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.getHorizontalWallRun().isWallRunning();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		if (parkourability.getHorizontalWallRun().isWallRightSide()) {
			transformer
					.addRotateLeftArm(0, 0, (float) Math.toRadians(-30))
					.makeArmsNatural()
					.rotateRightArm(0, 0, (float) Math.toRadians(60))
					.addRotateRightLeg(0, 0, (float) Math.toRadians(10))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(15))
					.end();
		} else {
			transformer
					.addRotateRightArm(0, 0, (float) Math.toRadians(30))
					.makeArmsNatural()
					.rotateLeftArm(0, 0, (float) Math.toRadians(-60))
					.addRotateRightLeg(0, 0, (float) Math.toRadians(-15))
					.addRotateLeftLeg(0, 0, (float) Math.toRadians(-10))
					.end();
		}
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float angle = 30 * (parkourability.getHorizontalWallRun().isWallRightSide() ? -1 : 1);
		rotator
				.startBasedCenter()
				.rotateRightward(angle)
				.end();
	}
}
