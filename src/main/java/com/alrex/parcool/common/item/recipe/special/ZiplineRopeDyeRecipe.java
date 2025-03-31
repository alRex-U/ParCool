package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ZiplineRopeDyeRecipe extends SpecialRecipe {
    public ZiplineRopeDyeRecipe(ResourceLocation p_i48169_1_) {
        super(p_i48169_1_);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory craftingInventory, @Nonnull World world) {
        boolean ziplineRopeFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack stack = craftingInventory.getItem(i);
            if (stack.getItem().equals(Items.ZIPLINE_ROPE.get())) {
                if (ziplineRopeFound) return false;
                else ziplineRopeFound = true;
            } else if (stack.getItem() instanceof DyeItem) {
                if (dyeItemFound) return false;
                else dyeItemFound = true;
            }
        }
        return ziplineRopeFound && dyeItemFound;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInventory craftingInventory) {
        ItemStack ziplineRope = null;
        DyeItem dyeItem = null;
        ItemStack dyeItemStack = null;
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack stack = craftingInventory.getItem(i);
            if (stack.getItem() instanceof ZiplineRopeItem) {
                ziplineRope = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                dyeItemStack = stack;
                dyeItem = (DyeItem) stack.getItem();
            }
        }
        if (ziplineRope == null || dyeItemStack == null) return ItemStack.EMPTY;
        ziplineRope = ziplineRope.copy();
        ZiplineRopeItem.setColor(ziplineRope, dyeItem.getDyeColor().getColorValue());
        return ziplineRope;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Recipes.ZIPLINE_ROPE_DYE.get();
    }
}
