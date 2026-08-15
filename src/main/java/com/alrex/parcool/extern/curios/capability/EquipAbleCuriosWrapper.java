package com.alrex.parcool.extern.curios.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
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

 */
