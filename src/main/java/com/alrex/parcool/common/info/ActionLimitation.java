package com.alrex.parcool.common.info;

public class ActionLimitation {
	private final boolean possible;
	private final int staminaConsumption;

	public boolean isPossible() {
		return possible;
	}

	public int getLeastStaminaConsumption() {
		return staminaConsumption;
	}

	public ActionLimitation(boolean possible, int leastStaminaConsumption) {
		this.possible = possible;
		this.staminaConsumption = leastStaminaConsumption;
	}
}
