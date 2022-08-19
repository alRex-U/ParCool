package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Breakfall extends Action {
	private boolean ready = false;
	private int readyTick = 0;
	private int readyCoolTick = 0;

	public boolean isReady() {
		return ready;
	}

	public int getReadyTick() {
		return readyTick;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (ready) {
			readyTick++;
		} else {
			readyTick = 0;
		}
	}

	public void startBreakfall(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		ready = false;
		readyTick = 0;
		synchronizeExplicitly(player);
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionBreakfall(), player);
		if (KeyBindings.getKeyForward().isDown()) {
			parkourability.getRoll().startRoll(player);
		} else {
			parkourability.getTap().startTap(player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			ready = parkourability.getPermission().canBreakfall() &&
					KeyBindings.getKeyBreakfall().isDown() &&
					!stamina.isExhausted() &&
					!parkourability.getCrawl().isCrawling() &&
					(!player.isOnGround() || parkourability.getAdditionalProperties().getLandingTick() < 3);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		ready = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(ready);
	}
}
