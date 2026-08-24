package com.alrex.parcool.common.item.misc;

import com.alrex.parcool.client.gui.GuiHelper;
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
import java.util.List;

public class ParCoolGuideItem extends Item {
    public ParCoolGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.guide.tooltip.guide").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.guide.tooltip.skilltree").withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        var itemInHand = player.getItemInHand(hand);
        if (level.isClientSide) {
            if (player.isShiftKeyDown()) {
                GuiHelper.openSkillTreeGui(player);
            } else {
                GuiHelper.openGuideGui();
            }
        }
        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
    }
}
