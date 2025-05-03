package com.alrex.parcool.common.capability.provider;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParkourabilityProvider implements ICapabilityProvider {
    private final LazyOptional<Parkourability> instanceOptional = LazyOptional.of(Parkourability::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        if (capability == Capabilities.PARKOURABILITY_CAPABILITY) {
            return instanceOptional.cast();
        }
        return LazyOptional.empty();
    }
}
