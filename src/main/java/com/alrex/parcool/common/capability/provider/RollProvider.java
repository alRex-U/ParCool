package com.alrex.parcool.common.capability.provider;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IRoll;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RollProvider implements ICapabilityProvider {
	public static final ResourceLocation CAPABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parcool.roll");

	private LazyOptional<IRoll> instance = LazyOptional.of(Capabilities.ROLL_CAPABILITY::getDefaultInstance);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == Capabilities.ROLL_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return cap == Capabilities.ROLL_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}
}
