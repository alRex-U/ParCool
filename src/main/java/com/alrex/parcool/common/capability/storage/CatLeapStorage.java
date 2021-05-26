package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.ICatLeap;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CatLeapStorage implements Capability.IStorage<ICatLeap> {
	@Override
	public void readNBT(Capability<ICatLeap> capability, ICatLeap instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<ICatLeap> capability, ICatLeap instance, Direction side) {
		return null;
	}
}
