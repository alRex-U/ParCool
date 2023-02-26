package com.alrex.parcool.common.capability.storage;

import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

;

public class ParkourabilityStorage {
	@Nullable
	public Tag writeTag(Capability<Parkourability> capability, Parkourability instance, Direction side) {
		return instance.getActionInfo().writeTag();
	}

	public void readTag(Capability<Parkourability> capability, Parkourability instance, Direction side, Tag nbt) {
		instance.getActionInfo().readTag(nbt);
	}
}
