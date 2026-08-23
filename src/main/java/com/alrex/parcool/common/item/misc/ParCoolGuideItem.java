package com.alrex.parcool.common.item.misc;

import com.alrex.parcool.ParCool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ParCoolGuideItem extends Item {
    public ParCoolGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.guide.tooltip.guide").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.guide.tooltip.skilltree").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        var itemInHand = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            ParCool.PROXY.openSkillTreeGui(player);
        } else {
            ParCool.PROXY.openGuideGui();
        }
        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
    }
}
