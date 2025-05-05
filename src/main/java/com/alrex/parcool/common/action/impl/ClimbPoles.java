package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class ClimbPoles extends Action {
	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		return false;
	}

	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return false;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
