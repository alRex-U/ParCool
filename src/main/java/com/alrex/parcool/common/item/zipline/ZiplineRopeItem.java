package com.alrex.parcool.common.item.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ZiplineRopeItem extends Item {

    public ZiplineRopeItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public static final int DEFAULT_COLOR = 0xFF4C7FE6;
    private static final DecimalFormat PERCENT_FORMATTER;

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        PERCENT_FORMATTER = new DecimalFormat("##0.0", decimalFormatSymbols);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        var posComponent = stack.getComponents().get(DataComponents.ZIPLINE_POSITION.get());

        if (posComponent != null) {
            lines.add(Component.translatable("parcool.gui.text.zipline.bind_pos", posComponent.pos().getX() + ", " + posComponent.pos().getY() + ", " + posComponent.pos().getZ()).withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.translatable("parcool.gui.text.zipline.not_bound").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.empty());
        lines.add(Component.translatable("parcool.gui.text.zipline.tension", getZiplineType(stack).getTranslationName()).withStyle(ChatFormatting.GRAY));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        // First Point is already registered
        if (hasBlockPosition(stack)) {
            // Second Point is Found
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ZiplineHookBlock) {
                BlockPos start = getBlockPosition(stack);
                if (start == null) return InteractionResult.FAIL;

                BlockPos end = context.getClickedPos();
                if (start.equals(end)) return InteractionResult.PASS;
                double horizontalDistSqr = Mth.square(start.getX() - end.getX()) + Mth.square(start.getZ() - end.getZ());
                if (horizontalDistSqr > Zipline.MAXIMUM_HORIZONTAL_DISTANCE * Zipline.MAXIMUM_HORIZONTAL_DISTANCE) {
                    if (context.getLevel().isClientSide()) {
                        Player player = context.getPlayer();
                        if (player != null) {
                            player.displayClientMessage(Component.translatable("parcool.message.zipline.too_far"), true);
                        }
                    }
                    return InteractionResult.FAIL;
                } else {
                    double verticalDist = Math.abs(end.getY() - start.getY());
                    if (verticalDist * Mth.fastInvSqrt(horizontalDistSqr) > 1. || verticalDist > Zipline.MAXIMUM_VERTICAL_DISTANCE) {
                        if (context.getLevel().isClientSide()) {
                            Player player = context.getPlayer();
                            if (player != null) {
                                player.displayClientMessage(Component.translatable("parcool.message.zipline.too_steep"), true);
                            }
                        }
                        return InteractionResult.FAIL;
                    }
                }

                BlockEntity startEntity = context.getLevel().getBlockEntity(start);
                BlockEntity endEntity = context.getLevel().getBlockEntity(end);
                if (startEntity instanceof ZiplineHookTileEntity startZipEntity && endEntity instanceof ZiplineHookTileEntity endZipEntity) {
                    if (getZiplineType(stack).getZipline(startZipEntity.getActualZiplinePoint(null), endZipEntity.getActualZiplinePoint(null)).conflictsWithSomething(context.getLevel())) {
                        Player player = context.getPlayer();
                        if (player != null) {
                            player.displayClientMessage(Component.translatable("parcool.message.zipline.obstacle_detected"), true);
                        }
                        return InteractionResult.FAIL;
                    }
                    if (!context.getLevel().isClientSide()) {
                        if (!startZipEntity.connectTo(endZipEntity, new ZiplineInfo(getZiplineType(stack), getColor(stack)))) {
                            Player player = context.getPlayer();
                            if (player != null) {
                                player.displayClientMessage(Component.translatable("parcool.message.zipline.already_exist"), true);
                            }
                            return InteractionResult.FAIL;
                        }
                        stack.shrink(1);
                    }
                    Player player = context.getPlayer();
                    if (player != null) {
                        player.playSound(SoundEvents.ZIPLINE_SET.get(), 1, 1);
                    }
                    removeBlockPosition(stack);
                    return InteractionResult.SUCCESS;
                } else {
                    removeBlockPosition(stack);
                    if (context.getLevel().isClientSide()) {
                        Player player = context.getPlayer();
                        if (player != null) {
                            player.displayClientMessage(Component.translatable("parcool.message.zipline.point_not_found"), true);
                        }
                    }
                    return InteractionResult.FAIL;
                }
            }
            // Remove position info
            if (context.isSecondaryUseActive()) {
                if (context.getLevel().isClientSide()) {
                    Player player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("parcool.message.zipline.reset_point"), true);
                    }
                }
                removeBlockPosition(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        } else {
            BlockPos pos = context.getClickedPos();
            if (context.getLevel().getBlockState(pos).getBlock() instanceof ZiplineHookBlock) {
                setBlockPosition(stack, pos);
                if (context.getLevel().isClientSide()) {
                    Player player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("parcool.message.zipline.set_point", pos.toShortString()), true);
                    }
                }
                return InteractionResult.SUCCESS;
            } else if (context.isSecondaryUseActive()) {
                changeZiplineType(stack);
                if (context.getLevel().isClientSide()) {
                    Player player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(Component.translatable("parcool.message.zipline.change_tension", getZiplineType(stack).getTranslationName()), true);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }

    public static void setBlockPosition(ItemStack stack, BlockPos pos) {
        stack.set(DataComponents.ZIPLINE_POSITION, new ZiplinePositionComponent(pos));
    }

    public static void removeBlockPosition(ItemStack stack) {
        stack.remove(DataComponents.ZIPLINE_POSITION);
    }

    public static boolean hasBlockPosition(ItemStack stack) {
        return stack.has(DataComponents.ZIPLINE_POSITION.get());
    }

    @Nullable
    public static BlockPos getBlockPosition(ItemStack stack) {
        var posComp = stack.getComponents().get(DataComponents.ZIPLINE_POSITION.get());
        return posComp == null ? null : posComp.pos();
    }

    public static void setColor(ItemStack stack, int color) {
        if (color != DEFAULT_COLOR) {
            stack.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new DyedItemColor(color, true));
        }else {
            stack.remove(net.minecraft.core.component.DataComponents.DYED_COLOR);
        }
    }

    public static int getColor(ItemStack stack) {
        var colorComp = stack.get(net.minecraft.core.component.DataComponents.DYED_COLOR);
        return colorComp == null ? DEFAULT_COLOR : colorComp.rgb();
    }

    public static boolean hasCustomColor(ItemStack stack) {
        var colorComp = stack.get(net.minecraft.core.component.DataComponents.DYED_COLOR);
        return colorComp != null && colorComp.rgb() != DEFAULT_COLOR;
    }

    public static ZiplineType getZiplineType(ItemStack stack) {
        var tensionComp = stack.get(DataComponents.ZIPLINE_TENSION);
        return tensionComp == null ? ZiplineType.STANDARD : tensionComp.type();
    }

    public static void changeZiplineType(ItemStack stack) {
        var currentType = getZiplineType(stack);
        setZiplineType(stack, ZiplineType.values()[(getZiplineType(stack).ordinal() + 1) % ZiplineType.values().length]);
    }

    public static void setZiplineType(ItemStack stack, ZiplineType type) {
        stack.set(DataComponents.ZIPLINE_TENSION, new ZiplineTensionComponent(type));
    }
}
