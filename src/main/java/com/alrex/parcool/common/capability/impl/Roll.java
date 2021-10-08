package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.ICrawl;
import com.alrex.parcool.common.capability.IRoll;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Roll implements IRoll {
	private boolean rollReady = false;
	private boolean rolling = false;
	private int rollingTime = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinueRollReady(PlayerEntity player) {
		ICrawl crawl = ICrawl.get(player);
		if (crawl == null) return false;

		return rollReady && ParCoolConfig.CONFIG_CLIENT.canRoll.get() && !crawl.isCrawling() && !crawl.isSliding() && KeyBindings.getKeyRoll().isKeyDown() && !rolling && !player.isInWaterOrBubbleColumn();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canRollReady(PlayerEntity player) {
		ICrawl crawl = ICrawl.get(player);
		if (crawl == null) return false;
		return !player.collidedVertically && ParCoolConfig.CONFIG_CLIENT.canRoll.get() && player.getMotion().getY() < -0.25 && !crawl.isCrawling() && !crawl.isSliding() && KeyBindings.getKeyRoll().isKeyDown() && !player.isInWaterOrBubbleColumn();
	}

	@Override
	public boolean isRollReady() {
		return rollReady;
	}

	@Override
	public boolean isRolling() {
		return rolling;
	}

	@Override
	public void setRollReady(boolean rollReady) {
		this.rollReady = rollReady;
	}

	@Override
	public void setRolling(boolean rolling) {
		this.rolling = rolling;
		if (!rolling) rollingTime = 0;
	}

	@Override
	public void updateRollingTime() {
		if (rolling) {
			rollingTime++;
			if (rollingTime > getRollAnimateTime()) {
				setRolling(false);
			}
		} else rollingTime = 0;
	}

	@Override
	public int getRollingTime() {
		return rollingTime;
	}

	@Override
	public int getStaminaConsumption() {
		return 50;
	}

	@Override
	public int getRollAnimateTime() {
		return 8;
	}

}
