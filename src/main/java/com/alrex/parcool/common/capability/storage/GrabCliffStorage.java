package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IGrabCliff;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class GrabCliffStorage implements Capability.IStorage<IGrabCliff> {
	@Override
	public void readNBT(Capability<IGrabCliff> capability, IGrabCliff instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IGrabCliff> capability, IGrabCliff instance, Direction side) {
		return null;
	}
}
