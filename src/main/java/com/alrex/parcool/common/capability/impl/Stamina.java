package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.IStamina;

public class Stamina implements IStamina {
	private static final int COOL_TIME = 20;

	private int stamina = getMaxStamina();
	private boolean exhausted = false;
	private int coolTime = 0;

	@Override
	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	@Override
	public int getStamina() {
		return stamina;
	}

	@Override
	public int getMaxStamina() {
		return ParCoolConfig.CONFIG_CLIENT.maxStamina.get();
	}

	@Override
	public void consume(int amount) {
		if (exhausted) return;
		stamina -= amount;
		coolTime = COOL_TIME;
		if (stamina <= 0) {
			stamina = 0;
			setExhausted(true);
		}
	}

	@Override
	public void recover(int amount) {
		if (coolTime > 0) return;

		stamina += amount;
		if (stamina >= getMaxStamina()) {
			stamina = getMaxStamina();
			setExhausted(false);
		}
	}

	@Override
	public void setExhausted(boolean exhausted) {
		this.exhausted = exhausted;
	}

	@Override
	public boolean isExhausted() {
		return exhausted;
	}

	@Override
	public void updateRecoveryCoolTime() {
		if (coolTime > 0) coolTime--;
	}

	@Override
	public int getRecoveryCoolTime() {
		return coolTime;
	}
}
