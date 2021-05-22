package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.IWallJump;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class WallJumpStorage implements Capability.IStorage<IWallJump> {
	@Override
	public void readNBT(Capability<IWallJump> capability, IWallJump instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<IWallJump> capability, IWallJump instance, Direction side) {
		return null;
	}
}
