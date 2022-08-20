package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class AdditionalProperties extends Action {
	private int sprintingTick = 0;
	private int notLandingTick = 0;
	private int landingTick = 0;

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isSprinting()) {
			sprintingTick++;
		} else {
			sprintingTick = 0;
		}
		if (player.isOnGround()) {
			notLandingTick = 0;
			landingTick++;
		} else {
			notLandingTick++;
			landingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {

	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	public int getSprintingTick() {
		return sprintingTick;
	}

	public int getNotLandingTick() {
		return notLandingTick;
	}

	public int getLandingTick() {
		return landingTick;
	}
}
