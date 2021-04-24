package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
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
		IStamina stamina = staminaOptional.resolve().get();
		ICrawl crawl = crawlOptional.resolve().get();

		return !stamina.isExhausted() && !crawl.isCrawling() && !crawl.isSliding() && player.isSprinting() && KeyBindings.getKeySprint().isKeyDown() && !player.isInWaterOrBubbleColumn();
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
