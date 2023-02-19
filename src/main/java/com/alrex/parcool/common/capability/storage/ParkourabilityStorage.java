package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ParkourabilityStorage implements Capability.IStorage<Parkourability> {
	@Nullable
	@Override
	public INBT writeNBT(Capability<Parkourability> capability, Parkourability instance, Direction side) {
		return instance.getActionInfo().writeNBT();
	}

	@Override
	public void readNBT(Capability<Parkourability> capability, Parkourability instance, Direction side, INBT nbt) {
		instance.getActionInfo().readNBT(nbt);
	}
}
