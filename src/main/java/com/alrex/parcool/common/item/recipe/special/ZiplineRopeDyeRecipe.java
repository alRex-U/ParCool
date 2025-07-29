package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;

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
                dyeItemFound = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return ziplineRopeFound && dyeItemFound;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInventory craftingInventory) {
        ItemStack ziplineRope = null;
        LinkedList<DyeItem> dyeItems = new LinkedList<>();
        for (int i = 0; i < craftingInventory.getContainerSize(); i++) {
            ItemStack stack = craftingInventory.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ZiplineRopeItem) {
                ziplineRope = stack;
            } else if (item instanceof DyeItem) {
                dyeItems.add((DyeItem) item);
            } else if (!stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (ziplineRope == null || dyeItems.isEmpty()) return ItemStack.EMPTY;
        ItemStack resultZiplineRope = new ItemStack(Items.ZIPLINE_ROPE::get);
        resultZiplineRope.setTag(ziplineRope.getTag());
        int r = 0, g = 0, b = 0;
        int dyeSize = dyeItems.size();
        for (DyeItem dyeItem : dyeItems) {
            int color = dyeItem.getDyeColor().getColorValue();
            r += (color & 0xFF0000) >> 16;
            g += (color & 0x00FF00) >> 8;
            b += (color & 0x0000FF);
        }
        if (ZiplineRopeItem.hasCustomColor(resultZiplineRope)) {
            dyeSize++;
            int color = ZiplineRopeItem.getColor(resultZiplineRope);
            r += (color & 0xFF0000) >> 16;
            g += (color & 0x00FF00) >> 8;
            b += (color & 0x0000FF);
        }
        r = MathHelper.clamp(r / dyeSize, 0, 0xFF);
        g = MathHelper.clamp(g / dyeSize, 0, 0xFF);
        b = MathHelper.clamp(b / dyeSize, 0, 0xFF);
        ZiplineRopeItem.setColor(resultZiplineRope, (r << 16) + (g << 8) + b);
        return resultZiplineRope;
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
