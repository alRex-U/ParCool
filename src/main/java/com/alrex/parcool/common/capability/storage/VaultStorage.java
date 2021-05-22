package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IVault;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class VaultStorage implements Capability.IStorage<IVault> {
	@Override
	public void readNBT(Capability<IVault> capability, IVault instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IVault> capability, IVault instance, Direction side) {
		return null;
	}
}
