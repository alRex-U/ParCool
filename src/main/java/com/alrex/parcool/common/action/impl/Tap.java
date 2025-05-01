package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;

import java.nio.ByteBuffer;

public class Tap extends Action {
	private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
	private boolean startRequired = false;

	@Override
	public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		player.multiplyDeltaMovement(0.01, 1, 0.01);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
		parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
	}

	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		startRequired = false;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new TapAnimator());
	}

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return startRequired;
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < getMaxTappingTick();
	}

	public void startTap(PlayerWrapper player) {
		startRequired = true;
	}

	public int getMaxTappingTick() {
		return 8;
	}
}
