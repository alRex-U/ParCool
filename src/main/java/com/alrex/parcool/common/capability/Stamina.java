package com.alrex.parcool.common.capability;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.network.SyncStaminaMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class Stamina {
	public static Stamina get(PlayerEntity player) {
		LazyOptional<Stamina> optional = player.getCapability(Capabilities.STAMINA_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private static final int COOL_TIME = 30;

	private final int maxStamina = 2000;
	private int stamina = getMaxStamina();
	private boolean exhausted = false;
	private int coolTime = 0;

	public int getStamina() {
		return stamina;
	}

	public int getMaxStamina() {
		return maxStamina;
	}

	public void setStamina(int stamina) {
		this.stamina = stamina;
	}

	public void consume(int amount, ActionInfo info) {
		if (exhausted || (info.isStaminaInfinite())) return;
		stamina -= amount;
		coolTime = COOL_TIME;
		if (stamina <= 0) {
			stamina = 0;
			exhausted = true;
		}
	}

	public void recover(int amount) {
		if (coolTime > 0) return;

		stamina += amount;
		if (stamina >= getMaxStamina()) {
			stamina = getMaxStamina();
			exhausted = false;
		}
	}

	public boolean isExhausted() {
		return exhausted;
	}

	public void onTick() {
		if (coolTime > 0) coolTime--;
	}

	public void synchronize(SyncStaminaMessage message) {
		this.exhausted = message.isExhausted();
		this.stamina = message.getStamina();
	}


	public int getRecoveryCoolTime() {
		return coolTime;
	}
}
