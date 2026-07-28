package com.alrex.parcool.common.item.misc;

import com.alrex.parcool.api.client.skilltree.PrepareParCoolSkillTreeEvent;
import com.alrex.parcool.client.gui.screen.ParCoolGuideScreen;
import com.alrex.parcool.client.gui.screen.SkillTreeScreen;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ParCoolGuideItem extends Item {
    public ParCoolGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        lines.add(Component.translatable("parcool.gui.text.guide.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                var prepareEvent = new PrepareParCoolSkillTreeEvent();
                MinecraftForge.EVENT_BUS.post(prepareEvent);
                Minecraft.getInstance().setScreen(new SkillTreeScreen(Parkourability.get(player).getCapabilities(), prepareEvent.getSkillTrees()));
            } else {
                Minecraft.getInstance().setScreen(new ParCoolGuideScreen(new ResourceLocation("parcool", "parcool_guide/welcome.md")));
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
