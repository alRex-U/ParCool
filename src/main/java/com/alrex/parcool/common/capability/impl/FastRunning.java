package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FastRunning implements IFastRunning {
	private boolean fastRunning = false;
	private int runningTime = 0;
	private int notRunningTime = 0;

	@Override
	public boolean isFastRunning() {
		return fastRunning;
	}

	@Override
	public void setFastRunning(boolean fastRunning) {
		this.fastRunning = fastRunning;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canFastRunning(ClientPlayerEntity player) {
		IStamina stamina = IStamina.get(player);
		ICrawl crawl = ICrawl.get(player);
		if (stamina == null || crawl == null) return false;

		return !stamina.isExhausted() && ParCoolConfig.CONFIG_CLIENT.canFastRunning.get() && !crawl.isCrawling() && !crawl.isSliding() && player.isSprinting() && KeyBindings.getKeySprint().isKeyDown() && !player.isInWaterOrBubbleColumn();
	}

	@Override
	public int getRunningTime() {
		return runningTime;
	}

	@Override
	public int getNotRunningTime() {
		return notRunningTime;
	}

	@Override
	public void updateTime() {
		if (isFastRunning()) {
			notRunningTime = 0;
			runningTime++;
		} else {
			runningTime = 0;
			notRunningTime++;
		}
	}

	@Override
	public int getStaminaConsumption() {
		return 4;
	}
}
