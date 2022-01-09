package com.alrex.parcool.common.info;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.network.ActionPermissionsMessage;

public class ActionInfo {
	private boolean received = false;
	private boolean allowedInfiniteStamina = false;

	public int getStaminaConsumptionCatLeap() {
		return 200;
	}

	public int getStaminaConsumptionClingToCliff() {
		return 4;
	}

	public int getStaminaConsumptionClimbUp() {
		return 200;
	}

	public int getStaminaConsumptionDodge() {
		return 100;
	}

	public int getStaminaConsumptionDodgeAvoid(float damage) {
		return Math.round(150 + damage * 30);
	}

	public int getStaminaConsumptionFastRun() {
		return 4;
	}

	public int getStaminaConsumptionWallJump() {
		return 200;
	}

	public double getCatLeapPower() {
		return 0.49;
	}

	public int getDodgeCoolTick() {
		return 10;
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
	}
}
