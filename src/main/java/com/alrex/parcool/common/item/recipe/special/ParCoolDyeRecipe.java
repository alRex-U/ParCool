package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.common.item.recipe.Recipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ParCoolDyeRecipe extends CustomRecipe {
    public ParCoolDyeRecipe(ResourceLocation location, CraftingBookCategory craftingBookCategory) {
        super(location, craftingBookCategory);
    }

    @Override
    public boolean matches(@Nonnull CraftingContainer craftingContainer, @Nonnull Level level) {
        boolean dyeAbleItemFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            if (stack.getItem() instanceof DyeAble) {
                if (dyeAbleItemFound) return false;
                else dyeAbleItemFound = true;
            } else if (stack.getItem() instanceof DyeItem) {
                dyeItemFound = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return dyeAbleItemFound && dyeItemFound;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        ItemStack dyeAbleStack = null;
        DyeAble dyeAbleItem = null;
        LinkedList<DyeItem> dyeItems = new LinkedList<>();
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            ItemStack stack = craftingContainer.getItem(i);
            Item item = stack.getItem();
            if (item instanceof DyeAble dyeAble) {
                dyeAbleStack = stack;
                dyeAbleItem = dyeAble;
            } else if (item instanceof DyeItem) {
                dyeItems.add((DyeItem) item);
            } else if (!stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        if (dyeAbleStack == null || dyeItems.isEmpty()) return ItemStack.EMPTY;
        ItemStack resultItem = new ItemStack(dyeAbleStack.getItem());
        var originalTag = dyeAbleStack.getTag();
        if (originalTag != null) {
            resultItem.setTag(originalTag.copy());
        }

        int r = 0, g = 0, b = 0;
        int dyeSize = dyeItems.size();
        for (DyeItem dyeItem : dyeItems) {
            DyeColor color = dyeItem.getDyeColor();
            r += (int) (color.getTextureDiffuseColors()[0] * 255f);
            g += (int) (color.getTextureDiffuseColors()[1] * 255f);
            b += (int) (color.getTextureDiffuseColors()[2] * 255f);
        }
        if (dyeAbleItem.hasCustomColor(resultItem)) {
            dyeSize++;
            int color = dyeAbleItem.getColor(resultItem);
            r += (color & 0xFF0000) >> 16;
            g += (color & 0x00FF00) >> 8;
            b += (color & 0x0000FF);
        }
        r = Mth.clamp(r / dyeSize, 0, 0xFF);
        g = Mth.clamp(g / dyeSize, 0, 0xFF);
        b = Mth.clamp(b / dyeSize, 0, 0xFF);
        dyeAbleItem.setColor(resultItem, (r << 16) + (g << 8) + b);
        return resultItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.DYE_ITEM.get();
    }
}
