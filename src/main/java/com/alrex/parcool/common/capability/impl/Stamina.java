package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.potion.Effects;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class Stamina implements IStamina {
	public Stamina(@Nullable Player player) {
		this.player = player;
		if (player != null && player.isLocalPlayer()) {
			maxStamina = ParCoolConfig.Client.Integers.MaxStamina.get();
			set(maxStamina);
		}
	}

	public Stamina() {
		this.player = null;
	}

	@Nullable
	private final Player player;

	private int stamina = 0;
	private int staminaOld = 0;
	private int maxStamina = 1;
	private boolean exhausted = false;

	@Override
	public int getMaxStamina() {
		return maxStamina;
	}

	@Override
	public int getActualMaxStamina() {
		if (player == null) return maxStamina;
		Parkourability parkourability = com.alrex.parcool.common.capability.Parkourability.get(player);
		if (parkourability == null) return maxStamina;
		return parkourability.getActionInfo().getMaxStamina();
	}

	@Override
	public void setMaxStamina(int value) {
		maxStamina = stamina;
	}

	@Override
	public int get() {
		return stamina;
	}

	@Override
	public int getOldValue() {
		return staminaOld;
	}

	@Override
	public void consume(int value) {
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (exhausted
				|| parkourability.getActionInfo().isStaminaInfinite(player.isSpectator() || player.isCreative())
				|| player.hasEffect(Effects.INEXHAUSTIBLE)
		) return;
		if (ParCoolConfig.Client.Booleans.UseHungerBarInstead.get()) {
			player.causeFoodExhaustion(value / 1000f);
			return;
		}
		recoverCoolTime = 30;
		set(stamina - value);
		if (stamina == 0) {
			exhausted = true;
		}
	}

	@Override
	public void recover(int value) {
		set(stamina + value);
		if (stamina == getActualMaxStamina()) {
			exhausted = false;
		}
	}

	@Override
	public boolean isExhausted() {
		return exhausted;
	}

	@Override
	public void setExhaustion(boolean value) {
		exhausted = value;
	}

	private int recoverCoolTime = 0;

	@Override
	public void tick() {
		staminaOld = stamina;
		if (recoverCoolTime > 0) recoverCoolTime--;
		if (recoverCoolTime <= 0) {
			if (player == null) return;
			com.alrex.parcool.common.capability.Parkourability parkourability = com.alrex.parcool.common.capability.Parkourability.get(player);
			if (parkourability == null) return;
			recover(parkourability.getActionInfo().getStaminaRecovery());
		}
	}

	@Override
	public void set(int value) {
		stamina = Math.min(value, getActualMaxStamina());
		if (stamina <= 0) {
			stamina = 0;
		}
	}
}
