package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.entity.player.PlayerEntity;

import java.nio.ByteBuffer;

public class BreakfallReady extends Action {
	public void startBreakfall(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		setDoing(false);
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionBreakfall(), player);
		if (KeyBindings.getKeyForward().isDown() && parkourability.getPermission().canRoll()) {
			parkourability.get(Roll.class).startRoll(player);
		} else {
			parkourability.get(Tap.class).startTap(player);
		}
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return (parkourability.getPermission().canBreakfall()
				&& KeyBindings.getKeyBreakfall().isDown()
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isInWaterOrBubble()
				&& (!player.isOnGround() || parkourability.get(AdditionalProperties.class).getLandingTick() < 3)
		);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}
}
