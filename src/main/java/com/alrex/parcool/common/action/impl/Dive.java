package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.DiveAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Dive extends Action {
	private boolean diving = false;
	private int divingTick = 0;
	private boolean falling = false;
	private boolean needAnimation = false;

	public boolean isDiving() {
		return diving;
	}

	public int getDivingTick() {
		return divingTick;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (diving) {
			divingTick++;
		} else {
			divingTick = 0;
		}
	}

	private boolean canDive(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return !stamina.isExhausted()
				&& !parkourability.getCrawl().isCrawling()
				&& !player.isVisuallyCrawling()
				&& parkourability.getFastRun().canActWithRunning(player)
				&& ParCoolConfig.CONFIG_CLIENT.canDive.get()
				&& WorldUtil.existsDivableSpace(player);
	}

	private boolean canContinueDive(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return !player.isFallFlying()
				&& !player.abilities.flying
				&& !player.isInWaterOrBubble()
				&& !player.isInLava()
				&& !player.isSwimming()
				&& !player.isOnGround()
				&& !stamina.isExhausted();
	}

	public void onJump(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (!player.isLocalPlayer()) return;
		if (canDive(player, parkourability, stamina)) {
			diving = true;
			needAnimation = true;
			synchronizeExplicitly(player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (needAnimation) {
			Animation animation = Animation.get(player);
			if (animation != null) {
				animation.setAnimator(new DiveAnimator(player.getDeltaMovement().y()));
			}
			needAnimation = false;
		}
		if (player.isLocalPlayer()) {
			if (!canContinueDive(player, parkourability, stamina)) {
				diving = false;
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		diving = BufferUtil.getBoolean(buffer);
		needAnimation = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(diving).putBoolean(needAnimation);
	}
}
