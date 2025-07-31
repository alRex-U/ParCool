package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ZiplineRopeDyeRecipe extends CustomRecipe {

    public ZiplineRopeDyeRecipe(CraftingBookCategory p_252125_) {
        super(p_252125_);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        boolean ziplineRopeFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
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
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        ItemStack ziplineRope = null;
        LinkedList<DyeItem> dyeItems = new LinkedList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
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
        resultZiplineRope.applyComponents(resultZiplineRope.getComponents());
        int r = 0, g = 0, b = 0;
        int dyeSize = dyeItems.size();
        for (DyeItem dyeItem : dyeItems) {
            int color = dyeItem.getDyeColor().getTextureDiffuseColor();
            r += FastColor.ARGB32.red(color);
            g += FastColor.ARGB32.green(color);
            b += FastColor.ARGB32.blue(color);
        }
        if (ZiplineRopeItem.hasCustomColor(resultZiplineRope)) {
            dyeSize++;
            int color = ZiplineRopeItem.getColor(resultZiplineRope);
            r += FastColor.ARGB32.red(color);
            g += FastColor.ARGB32.green(color);
            b += FastColor.ARGB32.blue(color);
        }
        r = Mth.clamp(r / dyeSize, 0, 0xFF);
        g = Mth.clamp(g / dyeSize, 0, 0xFF);
        b = Mth.clamp(b / dyeSize, 0, 0xFF);
        ZiplineRopeItem.setColor(resultZiplineRope, FastColor.ARGB32.color(r, g, b));
        return resultZiplineRope;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.ZIPLINE_ROPE_DYE.get();
    }
}
