package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

;

public class AdditionalProperties {
	private int sprintingTick = 0;
	private int notLandingTick = 0;
	private int landingTick = 0;
	private int notSprintingTick = 0;
	private int notCreativeFlyingTick = 0;
	private int tickAfterLastJump = 0;

	public void onJump() {
		tickAfterLastJump = 0;
	}

	public void onTick(Player player, Parkourability parkourability) {
		tickAfterLastJump++;
		if (player.isSprinting()) {
			notSprintingTick = 0;
			sprintingTick++;
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
		if (player.getAbilities().flying) {
			notCreativeFlyingTick = 0;
		} else {
			notCreativeFlyingTick++;
		}
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

	public int getNotSprintingTick() {
		return notSprintingTick;
	}

	public int getNotCreativeFlyingTick() {
		return notCreativeFlyingTick;
	}

	public int getTickAfterLastJump() {
		return tickAfterLastJump;
	}
}
