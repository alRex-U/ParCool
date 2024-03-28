package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class AdditionalProperties {
	private int sprintingTick = 0;
	private int notLandingTick = 0;
	private int landingTick = 0;
	private int lastSprintingTick = 0;
	private int notSprintingTick = 0;
	private int notCreativeFlyingTick = 0;
	private int inWaterTick = 0;
	private int notInWaterTick = 0;
	private int tickAfterLastJump = 0;

	public void onJump() {
		tickAfterLastJump = 0;
	}

	public void onTick(PlayerEntity player, Parkourability parkourability) {
		tickAfterLastJump++;
		if (player.isSprinting()) {
			notSprintingTick = 0;
			sprintingTick++;
			lastSprintingTick = sprintingTick;
		} else {
			sprintingTick = 0;
			notSprintingTick++;
		}
		if (player.isOnGround()) {
			notLandingTick = 0;
			landingTick++;
		} else {
			notLandingTick++;
			landingTick = 0;
		}
		if (player.abilities.flying) {
			notCreativeFlyingTick = 0;
		} else {
			notCreativeFlyingTick++;
		}
		if (player.isInWaterOrBubble()) {
			inWaterTick++;
			notInWaterTick = 0;
		} else {
			inWaterTick = 0;
			notInWaterTick++;
		}
	}

	public int getSprintingTick() {
		return sprintingTick;
	}

	public int getNotLandingTick() {
		return notLandingTick;
	}

	public int getLastSprintingTick() {
		return lastSprintingTick;
	}

	public int getLandingTick() {
		return landingTick;
	}

	public int getNotSprintingTick() {
		return notSprintingTick;
	}

	public int getNotCreativeFlyingTick() {
		return notCreativeFlyingTick;
	}

	public int getInWaterTick() {
		return inWaterTick;
	}

	public int getNotInWaterTick() {
		return notInWaterTick;
	}

	public int getTickAfterLastJump() {
		return tickAfterLastJump;
	}
}
