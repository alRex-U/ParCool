package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;

import java.nio.ByteBuffer;

public class ClimbPoles extends Action {
	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return false;
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return false;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
