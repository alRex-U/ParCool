package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
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
	public abstract boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo);

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina);

	public void onStart(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
	}

	public void onStartInServer(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
	}

	public void onStop(PlayerEntity player) {
	}

	public void onStopInServer(PlayerEntity player) {
	}

	public void onStopInOtherClient(PlayerEntity player) {
	}

	public void onStopInLocalClient(PlayerEntity player) {
	}

	public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	public void onWorkingTickInServer(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	public void onTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	public void onServerTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@OnlyIn(Dist.CLIENT)
	public boolean wantsToShowStatusBar(ClientPlayerEntity player, Parkourability parkourability) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public float getStatusValue(ClientPlayerEntity player, Parkourability parkourability) {
		return 0;
	}

	public abstract StaminaConsumeTiming getStaminaConsumeTiming();
}
