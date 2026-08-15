package com.alrex.parcool.extern.curios;

import com.alrex.parcool.common.item.armor.EquipAble;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CuriosManager extends ModManager {
    public CuriosManager() {
        super("curios");
    }

    /*
    public @Nullable ICapabilityProvider initEquipAbleCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (!isInstalled()) return null;
        if (!(stack.getItem() instanceof EquipAble)) return null;
        return new EquipAbleCuriosWrapper(stack);
    }
     */
}
