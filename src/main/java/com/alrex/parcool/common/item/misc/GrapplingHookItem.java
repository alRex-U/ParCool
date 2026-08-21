package com.alrex.parcool.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class GrapplingHookItem extends Item {
    public GrapplingHookItem(Properties properties) {
        super(properties);
    }

    public static boolean isHeld(Player player) {
        return player.getMainHandItem().getItem() instanceof GrapplingHookItem;
    }

    public static boolean isDeployed(net.minecraft.world.entity.LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        var parkourability = com.alrex.parcool.common.Parkourability.get(player);
        return parkourability != null
                && parkourability.get(com.alrex.parcool.common.action.ParCoolActions.GRAPPLE).isDoing();
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, Player player, @Nonnull InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ClientRendererHolder.get();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientRendererHolder {
        private static com.alrex.parcool.client.renderer.GrapplingHookItemRenderer renderer;

        private static com.alrex.parcool.client.renderer.GrapplingHookItemRenderer get() {
            if (renderer == null) renderer = new com.alrex.parcool.client.renderer.GrapplingHookItemRenderer();
            return renderer;
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> lines, @Nonnull TooltipFlag flag) {
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.throw").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.reel").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.steer").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("parcool.gui.text.grappling_hook.release").withStyle(ChatFormatting.GRAY));
    }
}
