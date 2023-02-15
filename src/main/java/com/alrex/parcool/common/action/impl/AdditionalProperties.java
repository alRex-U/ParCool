package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

import java.nio.ByteBuffer;

public class AdditionalProperties extends Action {
	private int sprintingTick = 0;
	private int notLandingTick = 0;
	private int landingTick = 0;
	private int notSprintingTick = 0;
	private int notCreativeFlyingTick = 0;

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return false;
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return false;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (player.isSprinting()) {
			notSprintingTick = 0;
			sprintingTick++;
		} else {
			sprintingTick = 0;
			notSprintingTick++;
		}
		if (player.isOnGround()) {
			notLandingTick = 0;
			landingTick++;
		} else {
			notLandingTick++;
			landingTick = 0;
		}
		if (player.abilities.flying) {
			notCreativeFlyingTick = 0;
		} else {
			notCreativeFlyingTick++;
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	public int getSprintingTick() {
		return sprintingTick;
	}

	public int getNotLandingTick() {
		return notLandingTick;
	}

	public int getLandingTick() {
		return landingTick;
	}

	public int getNotSprintingTick() {
		return notSprintingTick;
	}

	public int getNotCreativeFlyingTick() {
		return notCreativeFlyingTick;
	}
}
