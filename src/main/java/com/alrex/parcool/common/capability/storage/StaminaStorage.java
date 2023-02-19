package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StaminaStorage implements Capability.IStorage<IStamina> {
	@Nullable
	@Override
	public INBT writeNBT(Capability<IStamina> capability, IStamina instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("exhausted", instance.isExhausted());
		nbt.putInt("value", instance.get());
		return nbt;
	}

	@Override
	public void readNBT(Capability<IStamina> capability, IStamina instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT compound = (CompoundNBT) nbt;
			instance.set(compound.getInt("value"));
			instance.setExhaustion(compound.getBoolean("exhausted"));
		} else {
			throw new IllegalArgumentException("NBT for StaminaStorage, is not CompoundNBT");
		}
	}
}
