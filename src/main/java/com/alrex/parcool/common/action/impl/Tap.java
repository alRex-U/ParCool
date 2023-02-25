package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

import java.nio.ByteBuffer;

public class Tap extends Action {
	private boolean startRequired = false;

	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		player.setDeltaMovement(player.getDeltaMovement().multiply(0.01, 1, 0.01));
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return startRequired;
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < getMaxTappingTick();
	}

	public void startTap(PlayerEntity player) {
		startRequired = true;
	}

	public int getMaxTappingTick() {
		return 8;
	}
}
