package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dive extends Action {
	private boolean justJumped = false;
	private double initialYVelocityOfLastJump = 0.42;
	private double playerYSpeedOld = 0;
	private double playerYSpeed = 0;
	private int fallingTick = 0;

	public double getPlayerYSpeed(float partialTick) {
		return MathHelper.lerp(partialTick, playerYSpeedOld, playerYSpeed);
	}

	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		playerYSpeedOld = playerYSpeed;
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (isDoing() && (playerYSpeed < 0 || fallingTick > 0)) {
			fallingTick++;
		} else {
			fallingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean startInAir = player.getDeltaMovement().y() < 0
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 10
				&& parkourability.getAdditionalProperties().getNotInWaterTick() > 30
				&& KeyRecorder.keyJumpState.getTickKeyDown() > 10
				&& WorldUtil.existsSpaceBelow(player);
		if (!(startInAir || (justJumped && WorldUtil.existsDivableSpace(player) && parkourability.get(FastRun.class).canActWithRunning(player)))) {
			justJumped = false;
			return false;
		}

		startInfo.putDouble(initialYVelocityOfLastJump);
		BufferUtil.wrap(startInfo).putBoolean(startInAir);

		justJumped = false;
		return parkourability.getActionInfo().can(Dive.class)
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isVisuallyCrawling();
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
				|| (fallingTick > 5 && player.fallDistance < 0.1)
				|| stamina.isExhausted()
		);
	}

	public void onJump(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (!player.isLocalPlayer()) return;
		initialYVelocityOfLastJump = player.getDeltaMovement().y();
		justJumped = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double initialYSpeed = startData.getDouble();
		playerYSpeedOld = playerYSpeed = initialYSpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
		}
	}

	@Override
	public void onStop(PlayerEntity player) {
		if (player.isInWaterOrBubble()) {
			Animation animation = Animation.get(player);
			Parkourability parkourability = Parkourability.get(player);
			if (animation != null
					&& parkourability != null
					&& parkourability.getAdditionalProperties().getNotLandingTick() >= 5
					&& player.getDeltaMovement().y() < 0
			) {
				animation.setAnimator(new DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
			}
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
		double initialYSpeed = startData.getDouble();
		playerYSpeedOld = playerYSpeed = initialYVelocityOfLastJump = initialYSpeed;
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
		}
	}
}
