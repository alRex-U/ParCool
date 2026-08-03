package com.alrex.parcool.extern.curios.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class EquipAbleCuriosWrapper implements ICurio, ICapabilityProvider {
    private final LazyOptional<ICurio> holder = LazyOptional.of(() -> this);
    private final ItemStack itemStack;

    public EquipAbleCuriosWrapper(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        return CuriosCapability.ITEM.orEmpty(capability, holder);
    }

    @Override
    public ItemStack getStack() {
        return itemStack;
    }
}
