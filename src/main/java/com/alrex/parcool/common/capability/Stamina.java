package com.alrex.parcool.common.capability;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
		return 1000;
	}

	@Override
	public void consume(int amount) {
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

	@OnlyIn(Dist.CLIENT)
	public void syncState(ClientPlayerEntity player) {

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
