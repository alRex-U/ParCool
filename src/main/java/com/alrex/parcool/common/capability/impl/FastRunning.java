package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IFastRunning;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

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

	@Override
	public boolean canFastRunning(ClientPlayerEntity player) {

		LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
		LazyOptional<ICrawl> crawlOptional = player.getCapability(ICrawl.CrawlProvider.CRAWL_CAPABILITY);
		if (!staminaOptional.isPresent() || !crawlOptional.isPresent()) return false;
		IStamina stamina = staminaOptional.orElseThrow(NullPointerException::new);
		ICrawl crawl = crawlOptional.orElseThrow(NullPointerException::new);

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
