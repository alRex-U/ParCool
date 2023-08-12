package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dive extends Action {
	private boolean justJumped = false;
	private double playerYSpeedOld = 0;
	private double playerYSpeed = 0;

	public double getPlayerYSpeed(float partialTick) {
		return MathHelper.lerp(partialTick, playerYSpeedOld, playerYSpeed);
	}

	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		playerYSpeedOld = playerYSpeed;
		playerYSpeed = player.getDeltaMovement().y();
	}

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
		playerYSpeedOld = playerYSpeed = ySpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimationHostAnimator(ySpeed));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
		buffer.putDouble(playerYSpeed)
				.putDouble(playerYSpeedOld);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
		playerYSpeed = buffer.getDouble();
		playerYSpeedOld = buffer.getDouble();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		double ySpeed = startData.getDouble();
		playerYSpeedOld = playerYSpeed = ySpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimationHostAnimator(ySpeed));
		}
	}
}
