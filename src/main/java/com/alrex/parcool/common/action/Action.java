package com.alrex.parcool.common.action;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.nio.ByteBuffer;

public abstract class Action {
	private boolean doing = false;
	private int doingTick = 0;
	private int notDoingTick = 0;

	public boolean isJustStarted() {
		return isDoing() && getDoingTick() == 0;
	}

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
	public abstract boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo);

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canContinue(Player player, Parkourability parkourability);

    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
	}

	public void onStop(Player player) {
	}

	public void onStopInServer(Player player) {
	}

	public void onStopInOtherClient(Player player) {
	}

	public void onStopInLocalClient(Player player) {
	}

	public void onWorkingTick(Player player, Parkourability parkourability) {
	}

	public void onWorkingTickInServer(Player player, Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInClient(Player player, Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
	}

	public void onTick(Player player, Parkourability parkourability) {
	}

	public void onServerTick(Player player, Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
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
