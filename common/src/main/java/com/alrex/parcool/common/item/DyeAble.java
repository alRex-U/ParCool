package com.alrex.parcool.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DyeAble {
    class DyedColor implements ItemColor {
        private final int tintIndex;

        public DyedColor(int tintIndex) {
            this.tintIndex = tintIndex;
        }

        @Override
        public int getColor(ItemStack itemStack, int tintIndex) {
            if (tintIndex != this.tintIndex || !(itemStack.getItem() instanceof DyeAble dyeAble)) return -1;
            return dyeAble.getColor(itemStack);
        }
    }

    int getDefaultColor();

    default void setColor(ItemStack stack, int color) {
        var tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putInt("color", color);
    }

    default int getColor(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains("color")) {
            return getDefaultColor();
        }
        return tag.getInt("color");
    }

    default boolean hasCustomColor(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        return tag.contains("color");
    }

    static void appendHoverText(DyeAble dyeAble, ItemStack stack, @Nullable Level world, List<Component> lines, TooltipFlag flag) {
        if (dyeAble.hasCustomColor(stack)) {
            lines.add(Component.translatable("parcool.gui.text.dyeable.colored").withStyle(ChatFormatting.BLUE));
        } else {
            lines.add(Component.translatable("parcool.gui.text.dyeable.dyeable").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
