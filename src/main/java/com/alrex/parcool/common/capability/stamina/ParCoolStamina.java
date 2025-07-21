package com.alrex.parcool.common.capability.stamina;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class ParCoolStamina implements IStamina {
	public ParCoolStamina(@Nullable PlayerEntity player) {
		this.player = player;
		if (player != null && player.isLocalPlayer()) {
            set(Integer.MAX_VALUE);
		}
	}

	public ParCoolStamina() {
		this.player = null;
	}

	@Nullable
	private final PlayerEntity player;

	private int stamina = 0;
	private int staminaOld = 0;
	private boolean exhausted = false;

	@Override
	public int getActualMaxStamina() {
        if (player == null) return 1;
		Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return 1;
        ModifiableAttributeInstance attr = player.getAttribute(Attributes.MAX_STAMINA.get());
        if (attr == null) return 1;
        return Math.min((int) attr.getValue(), parkourability.getActionInfo().getMaxStaminaLimit());
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
                || player.hasEffect(Effects.INEXHAUSTIBLE.get())
		) return;
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
		if (recoverCoolTime > 0) recoverCoolTime--;
		if (recoverCoolTime <= 0) {
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
            ModifiableAttributeInstance attr = player.getAttribute(Attributes.STAMINA_RECOVERY.get());
            if (attr == null) return;
			if (player.isOnGround()) {
				recover(Math.min((int) attr.getValue(), parkourability.getActionInfo().getStaminaRecoveryLimit()));
			} else {
				recover(Math.min((int) attr.getValue(), parkourability.getActionInfo().getStaminaRecoveryLimit()) / 5);
			}
		}
	}

	@Override
	public void updateOldValue() {
		staminaOld = stamina;
	}

	@Override
	public void set(int value) {
		stamina = Math.min(value, getActualMaxStamina());
		if (stamina <= 0) {
			stamina = 0;
		}
	}
}
