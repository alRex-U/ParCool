package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;

public class RollAnimator extends Animator {
	private final float cameraPitch;

	public RollAnimator() {
		this.cameraPitch = 20;
	}

	public static float calculateMovementFactor(float progress) {
		return -MathUtil.squaring(progress - 1) + 1;
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getRoll().isRolling();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {

	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		Roll roll = parkourability.getRoll();
		float factor = calculateMovementFactor((roll.getRollingTick() + rotator.getPartialTick()) / (float) roll.getRollMaxTick());
		rotator
				.startBasedCenter()
				.rotateFrontward(MathUtil.lerp(0, 360, factor))
				.End();
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity clientPlayer, Parkourability parkourability) {
		Roll roll = parkourability.getRoll();
		if (roll.isRolling() && clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && !ParCoolConfig.CONFIG_CLIENT.disableCameraRolling.get()) {
			float factor = calculateMovementFactor((roll.getRollingTick() + event.renderTickTime) / (float) roll.getRollMaxTick());
			clientPlayer.xRot = (factor > 0.5 ? factor - 1 : factor) * 360f + cameraPitch;
		}
	}
}
