package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IDodge;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class DodgeStorage implements Capability.IStorage<IDodge> {
	@Override
	public void readNBT(Capability<IDodge> capability, IDodge instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IDodge> capability, IDodge instance, Direction side) {
		return null;
	}
}
