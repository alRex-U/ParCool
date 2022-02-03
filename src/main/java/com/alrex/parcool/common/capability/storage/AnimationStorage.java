package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.Animation;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class AnimationStorage implements Capability.IStorage<Animation> {
	@Nullable
	@Override
	public INBT writeNBT(Capability<Animation> capability, Animation instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<Animation> capability, Animation instance, Direction side, INBT nbt) {

	}
}
