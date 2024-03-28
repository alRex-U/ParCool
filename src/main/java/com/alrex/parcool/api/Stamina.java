package com.alrex.parcool.api;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class Stamina {
	@Nullable
	public static Stamina get(PlayerEntity player) {
		IStamina instance = IStamina.get(player);
		if (instance == null) {
			return null;
		}
		return new Stamina(instance);
	}

	private final IStamina staminaInstance;

	private Stamina(IStamina staminaInstance) {
		this.staminaInstance = staminaInstance;
	}

	public int getMaxValue() {
		return staminaInstance.getActualMaxStamina();
	}

	public int getValue() {
		return staminaInstance.get();
	}

	public boolean isExhausted() {
		return staminaInstance.isExhausted();
	}

	@OnlyIn(Dist.CLIENT)
	public void setValue(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > getMaxValue()) {
			value = getMaxValue();
		}
		staminaInstance.set(value);
	}

	@OnlyIn(Dist.CLIENT)
	public void consume(int value) {
		staminaInstance.consume(value);
	}

	@OnlyIn(Dist.CLIENT)
	public void recover(int value) {
		staminaInstance.recover(value);
	}
}
