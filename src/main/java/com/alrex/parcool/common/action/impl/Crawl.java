package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Crawl extends Action {
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		return (parkourability.getPermission().canCrawl()
				&& KeyRecorder.keyCrawlState.isPressed()
				&& !parkourability.getRoll().isDoing()
				&& !parkourability.getTap().isDoing()
				&& !player.isInWaterOrBubble()
				&& (player.isOnGround() || !ParCoolConfig.CONFIG_CLIENT.disableCrawlInAir.get())
		);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return KeyBindings.getKeyCrawl().isDown();
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new CrawlAnimator());
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void onWorkingTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		player.setSprinting(false);
		player.setPose(Pose.SWIMMING);
	}
}
