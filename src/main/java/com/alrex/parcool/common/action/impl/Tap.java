package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Tap extends Action {
	private boolean start = false;
	private boolean tapping = false;
	private int tappingTick = 0;

	public boolean isTapping() {
		return tapping;
	}

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (tapping) {
			tappingTick++;
		} else {
			tappingTick = 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if ((tapping && tappingTick <= 1) || start) {
			Animation animation = Animation.get(player);
			if (animation != null) animation.setAnimator(new TapAnimator());
		}
		if (start) {
			start = false;
			tapping = true;
		}
		if (player.isLocalPlayer()) {
			if (tapping) {
				player.setDeltaMovement(player.getDeltaMovement().scale(0.01));
			}
			if (tappingTick >= getMaxTappingTick()) tapping = false;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		start = BufferUtil.getBoolean(buffer);
		tapping = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(start).putBoolean(tapping);
	}

	public void startTap(Player player) {
		start = true;
	}

	public int getMaxTappingTick() {
		return 8;
	}
}
