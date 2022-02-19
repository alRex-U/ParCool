package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class StaminaStorage implements Capability.IStorage<Stamina> {
	@Nullable
	@Override
	public INBT writeNBT(Capability<Stamina> capability, Stamina instance, Direction side) {
		return IntNBT.valueOf(instance.getStamina());
	}

	@Override
	public void readNBT(Capability<Stamina> capability, Stamina instance, Direction side, INBT nbt) {
		if (nbt instanceof IntNBT) {
			instance.setStamina(((IntNBT) nbt).getInt());
		}
	}
}
