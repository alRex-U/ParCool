package com.alrex.parcool.compatibility;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainersWrapper {
    public static void dropItemStack(LevelWrapper level, double x, double y, double z, ItemStack stack) {
        InventoryHelper.dropItemStack(level.getInstance(), x,y, z, stack);
    }

    public static void dropItemStack(World level, double x, double y, double z, ItemStack stack) {
        InventoryHelper.dropItemStack(level, x, y, z, stack);
    }
}
