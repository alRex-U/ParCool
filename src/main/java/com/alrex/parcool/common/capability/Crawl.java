package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class Crawl implements ICrawl {
	private static final int maxSlidingTime = 15;

	private boolean crawling = false;
	private boolean sliding = false;
	private int slidingTime = -1;

	@Override
	public boolean isCrawling() {
		return crawling;
	}

	@Override
	public void setCrawling(boolean crawling) {
		this.crawling = crawling;
	}

	@Override
	public boolean isSliding() {
		return sliding;
	}

	@Override
	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}

	@Override
	public boolean canCrawl(ClientPlayerEntity player) {
		LazyOptional<IRoll> rollOptional = player.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
		if (!rollOptional.isPresent()) return false;
		IRoll roll = rollOptional.resolve().get();

		return KeyBindings.getKeyCrawl().isKeyDown() && ParCoolConfig.CONFIG_CLIENT.canCrawl.get() && !roll.isRolling() && !player.isInWaterOrBubbleColumn() && player.isOnGround();
	}

	@Override
	public boolean canSliding(ClientPlayerEntity player) {
		LazyOptional<IFastRunning> fastOptional = player.getCapability(IFastRunning.FastRunningProvider.FAST_RUNNING_CAPABILITY);
		LazyOptional<IRoll> rollOptional = player.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
		if (!fastOptional.isPresent() || !rollOptional.isPresent()) return false;
		IFastRunning fastRunning = fastOptional.resolve().get();
		IRoll roll = rollOptional.resolve().get();

		if (!isSliding() && ParCoolConfig.CONFIG_CLIENT.canCrawl.get() && fastRunning.isFastRunning() && !roll.isRollReady() && !roll.isRolling() && player.isOnGround() && KeyBindings.getKeyCrawl().isKeyDown() && slidingTime >= 0) {
			return true;
		}
		if (isSliding() && slidingTime <= maxSlidingTime) return true;
		return false;
	}

	@Override
	public void updateSlidingTime(ClientPlayerEntity player) {
		if (slidingTime < 0 && KeyBindings.getKeyCrawl().isKeyDown()) return;

		if (isSliding()) slidingTime++;
		else slidingTime = 0;
		if ((slidingTime > maxSlidingTime && player.isOnGround()) || player.isInWaterOrBubbleColumn()) {
			slidingTime = -1;
			setSliding(false);
		}
	}
}
