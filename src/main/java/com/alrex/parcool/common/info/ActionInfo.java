package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.network.ActionPermissionsMessage;

public class ActionInfo {
	private boolean received = false;
	private boolean allowedInfiniteStamina = false;
	private int dodgeCoolTick = 0;
	private int staminaConsumeCatleap;
	private int staminaConsumeClimbUp;
	private int staminaConsumeClingToCliff;
	private int staminaConsumeDodge;
	private int staminaConsumeFastRun;
	private int staminaConsumeWallJump;
	private int staminaConsumeWallSlide;

	public int getStaminaConsumptionCatLeap() {
		return staminaConsumeCatleap;
	}

	public int getStaminaConsumptionClingToCliff() {
		return staminaConsumeClingToCliff;
	}

	public int getStaminaConsumptionClimbUp() {
		return staminaConsumeClimbUp;
	}

	public int getStaminaConsumptionDodge() {
		return staminaConsumeDodge;
	}

	public int getStaminaConsumptionDodgeAvoid(float damage) {
		return Math.round(150 + damage * 30);
	}

	public int getStaminaConsumptionFastRun() {
		return staminaConsumeFastRun;
	}

	public int getStaminaConsumptionWallJump() {
		return staminaConsumeWallJump;
	}

	public int getStaminaConsumeWallSlide() {
		return staminaConsumeWallSlide;
	}

	public double getCatLeapPower() {
		return 0.49;
	}

	public int getDodgeCoolTick() {
		return dodgeCoolTick;
	}

	public int getMaxSlidingTick() {
		return 15;
	}

	public boolean isStaminaInfinite() {
		return ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get() && allowedInfiniteStamina;
	}

	public void receiveServerPermissions(ActionPermissionsMessage message) {
		received = true;
		allowedInfiniteStamina = message.isAllowedInfiniteStamina();
		dodgeCoolTick = message.getDodgeCoolTick();
		staminaConsumeCatleap = message.getStaminaConsumeCatleap();
		staminaConsumeClimbUp = message.getStaminaConsumeClimbUp();
		staminaConsumeClingToCliff = message.getStaminaConsumeClingToCliff();
		staminaConsumeDodge = message.getStaminaConsumeDodge();
		staminaConsumeFastRun = message.getStaminaConsumeFastRun();
		staminaConsumeWallJump = message.getStaminaConsumeWallJump();
		staminaConsumeWallSlide = message.getStaminaConsumeWallSlide();
	}
}
