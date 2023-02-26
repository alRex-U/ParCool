package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.DiveAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

;

public class Dive extends Action {
	private boolean justJumped = false;
	private double playerYSpeed = 0;

	public double getPlayerYSpeed() {
		return playerYSpeed;
	}

	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
		if (isDoing() && player.isLocalPlayer()) {
			playerYSpeed = player.getDeltaMovement().y;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean can = (justJumped
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isVisuallyCrawling()
				&& parkourability.get(FastRun.class).canActWithRunning(player)
				&& parkourability.getActionInfo().can(Dive.class)
				&& WorldUtil.existsDivableSpace(player)
		);
		startInfo.putDouble(player.getDeltaMovement().y);
		justJumped = false;
		return can;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return !(player.isFallFlying()
				|| player.getAbilities().flying
				|| player.isInWaterOrBubble()
				|| player.isInLava()
				|| player.isSwimming()
				|| player.isOnGround()
				|| stamina.isExhausted()
		);
	}

	public void onJump(Player player, Parkourability parkourability, IStamina stamina) {
		if (!player.isLocalPlayer()) return;
		justJumped = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
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

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
		buffer.putDouble(playerYSpeed);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
		playerYSpeed = buffer.getDouble();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		double ySpeed = startData.getDouble();
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimator(ySpeed));
		}
	}
}
