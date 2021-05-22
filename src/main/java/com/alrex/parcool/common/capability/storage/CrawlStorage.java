package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.ICrawl;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CrawlStorage implements Capability.IStorage<ICrawl> {
	@Override
	public void readNBT(Capability<ICrawl> capability, ICrawl instance, Direction side, INBT nbt) {
	}

	@Nullable
	@Override
	public INBT writeNBT(Capability<ICrawl> capability, ICrawl instance, Direction side) {
		return null;
	}
}
