package com.alrex.parcool.forge.extern.curios;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.forge.extern.ModManager;
import com.alrex.parcool.forge.extern.curios.capability.EquipAbleCuriosWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class CuriosManager extends ModManager {
    public CuriosManager() {
        super("curios");
    }

    public @Nullable ICapabilityProvider initEquipAbleCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (!isInstalled()) return null;
        if (!(stack.getItem() instanceof EquipAble)) return null;
        return new EquipAbleCuriosWrapper(stack);
    }
}
