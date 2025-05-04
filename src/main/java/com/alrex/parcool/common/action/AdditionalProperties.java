package com.alrex.parcool.common.action;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;

public class AdditionalProperties {
	private int sprintingTick = 0;
	private int notLandingTick = 0;
	private int previousNotLandingTick = Integer.MAX_VALUE;
	private int landingTick = 0;
	private int lastSprintingTick = 0;
	private int notSprintingTick = 0;
	private int notCreativeFlyingTick = 0;
    private int sneakingTick = 0;
    private int notSneakingTick = 0;
    private int lastSneakingTick = 0;
	private int inWaterTick = 0;
	private int notInWaterTick = 0;
	private int tickAfterLastJump = 0;

	public void onJump() {
		tickAfterLastJump = 0;
	}

	public void onTick(Player player, Parkourability parkourability) {
		tickAfterLastJump++;
		if (player.isSprinting()) {
			notSprintingTick = 0;
			sprintingTick++;
			lastSprintingTick = sprintingTick;
		} else {
			sprintingTick = 0;
			notSprintingTick++;
		}
        if (player.isShiftKeyDown()) {
            sneakingTick++;
            notSneakingTick = 0;
            lastSneakingTick = sneakingTick;
        } else {
            sneakingTick = 0;
            notSneakingTick++;
        }
		if (player.onGround()) {
			if (notLandingTick > 0) {
				previousNotLandingTick = notLandingTick;
			}
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

	public int getPreviousNotLandingTick() {
		return previousNotLandingTick;
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

    public int getSneakingTick() {
        return sneakingTick;
    }

    public int getNotSneakingTick() {
        return notSneakingTick;
    }

    public int getLastSneakingTick() {
        return lastSneakingTick;
    }
}
