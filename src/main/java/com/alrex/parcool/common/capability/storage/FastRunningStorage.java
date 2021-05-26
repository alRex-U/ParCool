package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IFastRunning;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class FastRunningStorage implements Capability.IStorage<IFastRunning> {
	@Override
	public void readNBT(Capability<IFastRunning> capability, IFastRunning instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IFastRunning> capability, IFastRunning instance, Direction side) {
		return null;
	}
}
