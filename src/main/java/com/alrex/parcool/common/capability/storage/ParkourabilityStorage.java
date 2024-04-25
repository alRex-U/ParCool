package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

;

public class ParkourabilityStorage {
	@Nullable
	@Override
	public INBT writeNBT(Capability<Parkourability> capability, Parkourability instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<Parkourability> capability, Parkourability instance, Direction side, INBT nbt) {
	}
}
