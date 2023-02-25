package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.DiveAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dive extends Action {
	private boolean justJumped = false;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean can = (justJumped
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isVisuallyCrawling()
				&& parkourability.get(FastRun.class).canActWithRunning(player)
				&& parkourability.getActionInfo().can(Dive.class)
				&& WorldUtil.existsDivableSpace(player)
		);
		startInfo.putDouble(player.getDeltaMovement().y());
		justJumped = false;
		return can;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return !(player.isFallFlying()
				|| player.abilities.flying
				|| player.isInWaterOrBubble()
				|| player.isInLava()
				|| player.isSwimming()
				|| player.isOnGround()
				|| stamina.isExhausted()
		);
	}

	public void onJump(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (!player.isLocalPlayer()) return;
		justJumped = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double ySpeed = startData.getDouble();
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimator(ySpeed));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		double ySpeed = startData.getDouble();
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimator(ySpeed));
		}
	}
}
