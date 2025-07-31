package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ZiplineRopeDyeRecipe extends CustomRecipe {
    public ZiplineRopeDyeRecipe(ResourceLocation p_i48169_1_) {
        super(p_i48169_1_);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer craftingContainer, @Nonnull Level level) {
        boolean ziplineRopeFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
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

    @Override
    public net.minecraft.world.item.ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack ziplineRope = null;
        LinkedList<DyeItem> dyeItems = new LinkedList<>();
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
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
            DyeColor color = dyeItem.getDyeColor();
            r += (int) (color.getTextureDiffuseColors()[0] * 255f);
            g += (int) (color.getTextureDiffuseColors()[1] * 255f);
            b += (int) (color.getTextureDiffuseColors()[2] * 255f);
        }
        if (ZiplineRopeItem.hasCustomColor(resultZiplineRope)) {
            dyeSize++;
            int color = ZiplineRopeItem.getColor(resultZiplineRope);
            r += (color & 0xFF0000) >> 16;
            g += (color & 0x00FF00) >> 8;
            b += (color & 0x0000FF);
        }
        r = Mth.clamp(r / dyeSize, 0, 0xFF);
        g = Mth.clamp(g / dyeSize, 0, 0xFF);
        b = Mth.clamp(b / dyeSize, 0, 0xFF);
        ZiplineRopeItem.setColor(resultZiplineRope, (r << 16) + (g << 8) + b);
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
