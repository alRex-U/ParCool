package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

;

public class Tap extends Action {
	private boolean startRequired = false;

	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
		player.setDeltaMovement(player.getDeltaMovement().multiply(0.01, 1, 0.01));
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
        parkourability.getCancelMarks().addMarkerCancellingJump(this::isDoing);
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return startRequired;
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < getMaxTappingTick();
	}

	public void startTap(Player player) {
		startRequired = true;
	}

	public int getMaxTappingTick() {
		return 8;
	}
}
