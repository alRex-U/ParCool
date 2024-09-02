package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
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
	public abstract boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo);

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canContinue(Player player, Parkourability parkourability, IStamina stamina);

	public void onStart(Player player, Parkourability parkourability) {
	}

	public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
	}

	public void onStop(Player player) {
	}

	public void onStopInServer(Player player) {
	}

	public void onStopInOtherClient(Player player) {
	}

	public void onStopInLocalClient(Player player) {
	}

	public void onWorkingTick(Player player, Parkourability parkourability, IStamina stamina) {
	}

	public void onWorkingTickInServer(Player player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInClient(Player player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
	}

	public void onTick(Player player, Parkourability parkourability, IStamina stamina) {
	}

	public void onServerTick(Player player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
	}

	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	public void saveSynchronizedState(ByteBuffer buffer) {
	}

    @OnlyIn(Dist.CLIENT)
    public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
        return 0;
    }

	public abstract StaminaConsumeTiming getStaminaConsumeTiming();
}
