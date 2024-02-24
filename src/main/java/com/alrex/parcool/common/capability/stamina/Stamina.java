package com.alrex.parcool.common.capability.stamina;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class Stamina implements IStamina {
    public Stamina(Player player) {
		this.player = player;
		if (player != null && player.isLocalPlayer()) {
            set(Integer.MAX_VALUE);
        }
	}

	private final Player player;

	private int stamina = 0;
	private int staminaOld = 0;
	private boolean exhausted = false;

	@Override
	public int getActualMaxStamina() {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return 1;
        AttributeInstance attr = player.getAttribute(Attributes.MAX_STAMINA.get());
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
		staminaOld = stamina;
		if (recoverCoolTime > 0) recoverCoolTime--;
		if (recoverCoolTime <= 0) {
			if (player == null) return;
			com.alrex.parcool.common.capability.Parkourability parkourability = com.alrex.parcool.common.capability.Parkourability.get(player);
			if (parkourability == null) return;
            AttributeInstance attr = player.getAttribute(Attributes.STAMINA_RECOVERY.get());
            if (attr == null) return;
            recover(Math.min((int) attr.getValue(), parkourability.getActionInfo().getStaminaRecoveryLimit()));
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
