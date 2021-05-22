package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StaminaStorage implements Capability.IStorage<IStamina> {
	private static final String STAMINA = "stamina";
	private static final String EXHAUSTED = "exhausted";

	@Override
	public void readNBT(Capability<IStamina> capability, IStamina instance, Direction side, INBT nbt) {
		CompoundNBT compoundNBT = (CompoundNBT) nbt;
		instance.setStamina(compoundNBT.getInt(STAMINA));
		instance.setExhausted(compoundNBT.getBoolean(EXHAUSTED));
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IStamina> capability, IStamina instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(STAMINA, instance.getStamina());
		nbt.putBoolean(EXHAUSTED, instance.isExhausted());
		return nbt;
	}
}
