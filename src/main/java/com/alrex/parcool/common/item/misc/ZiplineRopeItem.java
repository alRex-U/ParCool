package com.alrex.parcool.common.item.misc;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.common.item.ParCoolDataComponents;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineInfo;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ZiplineRopeItem extends Item implements DyeAble {
    @Override
    public int getDefaultColor() {
        return DEFAULT_COLOR;
    }

    public static ItemStack from(ZiplineInfo info) {
        var stack = new ItemStack(ParCoolItems.ZIPLINE_ROPE::get);
        ParCoolItems.ZIPLINE_ROPE.get().setColor(stack, info.color());
        return stack;
    }

    public ZiplineRopeItem(Properties properties) {
        super(properties);
    }

    public static final int DEFAULT_COLOR = 0x4C7FE6;
    private static final DecimalFormat PERCENT_FORMATTER;
    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        PERCENT_FORMATTER = new DecimalFormat("##0.0", decimalFormatSymbols);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
        var boundPosComp = stack.get(ParCoolDataComponents.ZIPLINE_POSITION);
        if (boundPosComp != null) {
            lines.add(Component.translatable("parcool.gui.text.zipline.bind_pos", boundPosComp.pos().toShortString()).withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.translatable("parcool.gui.text.zipline.not_bound").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.translatable("parcool.gui.text.zipline.tension", getZiplineType(stack).getTranslationName()).withStyle(ChatFormatting.GRAY));
        DyeAble.appendHoverText(this, stack, context, lines, tooltipFlag);
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
                    if (verticalDist * Mth.invSqrt(horizontalDistSqr) > 1. || verticalDist > Zipline.MAXIMUM_VERTICAL_DISTANCE) {
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
                    if (getZiplineType(stack).getZipline(startZipEntity.getHookPoint(), endZipEntity.getHookPoint()).conflictsWithSomething(context.getLevel())) {
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
                        player.playSound(ParCoolSoundEvents.ZIPLINE_SET.get(), 1, 1);
                    }
                    removeBlockPosition(stack);
                    return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
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
        stack.set(ParCoolDataComponents.ZIPLINE_POSITION, new ZiplinePositionComponent(pos));
    }

    public static void removeBlockPosition(ItemStack stack) {
        stack.remove(ParCoolDataComponents.ZIPLINE_POSITION);
    }

    public static boolean hasBlockPosition(ItemStack stack) {
        return stack.has(ParCoolDataComponents.ZIPLINE_POSITION);
    }

    @Nullable
    public static BlockPos getBlockPosition(ItemStack stack) {
        var pos = stack.get(ParCoolDataComponents.ZIPLINE_POSITION);
        return pos == null ? null : pos.pos();
    }

    public static ZiplineType getZiplineType(ItemStack stack) {
        var comp = stack.get(ParCoolDataComponents.ZIPLINE_TENSION);
        return comp != null ? comp.type() : ZiplineType.STANDARD;
    }

    public static void changeZiplineType(ItemStack stack) {
        stack.set(ParCoolDataComponents.ZIPLINE_TENSION, new ZiplineTensionComponent(ZiplineType.values()[(byte) ((getZiplineType(stack).ordinal() + 1) % ZiplineType.values().length)]));
    }

    public static void setZiplineType(ItemStack stack, ZiplineType type) {
        stack.set(ParCoolDataComponents.ZIPLINE_TENSION, new ZiplineTensionComponent(type));
    }
}
