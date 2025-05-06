package com.alrex.parcool.common.capability.provider;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.stamina.OtherStamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StaminaProvider implements ICapabilitySerializable<CompoundNBT> {
    public StaminaProvider(PlayerEntity player) {
        IStamina instance;
        if (player.isLocalPlayer()) {
            instance = ParCoolConfig.Client.StaminaType.get().newInstance(player);
        } else {
            instance = new OtherStamina(player);
        }
        instanceOptional = LazyOptional.of(() -> instance);
    }

    private final LazyOptional<IStamina> instanceOptional;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        if (capability == Capabilities.STAMINA_CAPABILITY) {
            return instanceOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.STAMINA_CAPABILITY.getStorage().writeNBT(
                Capabilities.STAMINA_CAPABILITY,
                instanceOptional.resolve().get(),
                null
        );
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.STAMINA_CAPABILITY.getStorage().readNBT(
                Capabilities.STAMINA_CAPABILITY,
                instanceOptional.resolve().get(),
                null,
                nbt
        );
    }
}
