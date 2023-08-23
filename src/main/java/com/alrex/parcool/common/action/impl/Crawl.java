package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Crawl extends Action {
	public enum ControlType {
		PressKey, Toggle
	}

	public boolean toggleStatus = false;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return ((ParCoolConfig.Client.CrawlControl.get() == ControlType.PressKey && KeyRecorder.keyCrawlState.isPressed())
				|| (ParCoolConfig.Client.CrawlControl.get() == ControlType.Toggle && toggleStatus))
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& parkourability.get(Vault.class).getNotDoingTick() >= 8
				&& player.getVehicle() == null
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& (player.isOnGround() || ParCoolConfig.Client.Booleans.EnableCrawlInAir.get());
	}

	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (player.isLocalPlayer()) {
			if (ParCoolConfig.Client.CrawlControl.get() == Crawl.ControlType.Toggle) {
				if (KeyRecorder.keyCrawlState.isPressed())
					toggleStatus = !toggleStatus;
			} else {
				toggleStatus = false;
			}
		}
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return (ParCoolConfig.Client.CrawlControl.get() == ControlType.PressKey && KeyBindings.getKeyCrawl().isDown())
				|| (ParCoolConfig.Client.CrawlControl.get() == ControlType.Toggle && toggleStatus);
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new CrawlAnimator());
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}

	@Override
	public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		player.setSprinting(false);
		player.setPose(Pose.SWIMMING);
	}
}
