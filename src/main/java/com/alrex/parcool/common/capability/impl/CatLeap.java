package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.ICatLeap;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CatLeap implements ICatLeap {
	private boolean leaping = false;
	private boolean ready = false;
	private int readyTime = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canCatLeap(PlayerEntity player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return false;
		return player.collidedVertically && ParCoolConfig.CONFIG_CLIENT.canCatLeap.get() && !stamina.isExhausted() && ready && readyTime < 10 && !KeyBindings.getKeySneak().isKeyDown();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canReadyLeap(PlayerEntity player) {
		IFastRunning fastRunning = IFastRunning.get(player);
		if (fastRunning == null) return false;
		return (fastRunning.getNotRunningTime() < 10 && KeyBindings.getKeySneak().isKeyDown()) || (ready && KeyBindings.getKeySneak().isKeyDown() && readyTime < 10);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public double getBoostValue(PlayerEntity player) {
		return 0.49;
	}

	@Override
	public boolean isLeaping() {
		return leaping;
	}

	@Override
	public void setLeaping(boolean leaping) {
		this.leaping = leaping;
	}

	@Override
	public int getReadyTime() {
		return readyTime;
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	@Override
	public void updateReadyTime() {
		if (ready) readyTime++;
		else readyTime = 0;
	}

	@Override
	public int getStaminaConsumption() {
		return 200;
	}
}
