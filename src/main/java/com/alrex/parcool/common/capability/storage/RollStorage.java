package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IRoll;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class RollStorage implements Capability.IStorage<IRoll> {
	@Override
	public void readNBT(Capability<IRoll> capability, IRoll instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IRoll> capability, IRoll instance, Direction side) {
		return null;
	}
}
