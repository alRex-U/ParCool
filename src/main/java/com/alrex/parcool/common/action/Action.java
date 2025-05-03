package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	private boolean doing = false;
	private int doingTick = 0;
	private int notDoingTick = 0;

	public void setDoingTick(int doingTick) {
		this.doingTick = doingTick;
	}

	public void setNotDoingTick(int notDoingTick) {
		this.notDoingTick = notDoingTick;
	}

	public int getDoingTick() {
		return doingTick;
	}

	public int getNotDoingTick() {
		return notDoingTick;
	}

	public boolean isDoing() {
		return doing;
	}

	public void setDoing(boolean value) {
		doing = value;
	}

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo);

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina);

	public void onStart(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
	}

	public void onStartInServer(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
	}

	public void onStop(PlayerWrapper player) {
	}

	public void onStopInServer(PlayerWrapper player) {
	}

	public void onStopInOtherClient(PlayerWrapper player) {
	}

	public void onStopInLocalClient(PlayerWrapper player) {
	}

	public void onWorkingTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	public void onWorkingTickInServer(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	public void onTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	public void onServerTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerWrapper player, Parkourability parkourability) {
	}

	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@OnlyIn(Dist.CLIENT)
	public boolean wantsToShowStatusBar(ClientPlayerWrapper player, Parkourability parkourability) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public float getStatusValue(ClientPlayerWrapper player, Parkourability parkourability) {
		return 0;
	}

	public abstract StaminaConsumeTiming getStaminaConsumeTiming();
}
