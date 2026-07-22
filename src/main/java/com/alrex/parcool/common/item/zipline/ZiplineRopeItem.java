package com.alrex.parcool.common.item.zipline;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.item.DyeAble;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineInfo;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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

    public ZiplineRopeItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public static final int DEFAULT_COLOR = 0x4C7FE6;
    private static final DecimalFormat PERCENT_FORMATTER;

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        PERCENT_FORMATTER = new DecimalFormat("##0.0", decimalFormatSymbols);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> lines, TooltipFlag flag) {
        var tag = stack.getTag();

        if (tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z")) {
            lines.add(Component.translatable("parcool.gui.text.zipline.bind_pos", tag.getInt("Tile_X") + ", " + tag.getInt("Tile_Y") + ", " + tag.getInt("Tile_Z")).withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.translatable("parcool.gui.text.zipline.not_bound").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.translatable("parcool.gui.text.zipline.tension", getZiplineType(stack).getTranslationName()).withStyle(ChatFormatting.GRAY));
        DyeAble.appendHoverText(this, stack, world, lines, flag);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab tab, @Nonnull NonNullList<ItemStack> list) {
        if (this.allowedIn(tab)) {
            list.add(new ItemStack(this));
            for (var dye : DyeColor.values()) {
                var itemStack = new ItemStack(this);
                int r = (int) (dye.getTextureDiffuseColors()[0] * 255f);
                int g = (int) (dye.getTextureDiffuseColors()[1] * 255f);
                int b = (int) (dye.getTextureDiffuseColors()[2] * 255f);
                this.setColor(itemStack, (r << 16) + (g << 8) + b);
                list.add(itemStack);
            }
        }

    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getTag();

        // First Point is already registered
        if (tag != null && hasBlockPosition(stack)) {
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
        var tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putInt("Tile_X", pos.getX());
        tag.putInt("Tile_Y", pos.getY());
        tag.putInt("Tile_Z", pos.getZ());
    }

    public static void removeBlockPosition(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) {
            return;
        }
        tag.remove("Tile_X");
        tag.remove("Tile_Y");
        tag.remove("Tile_Z");
    }

    public static boolean hasBlockPosition(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z");
    }

    @Nullable
    public static BlockPos getBlockPosition(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) {
            return null;
        }
        return new BlockPos(tag.getInt("Tile_X"), tag.getInt("Tile_Y"), tag.getInt("Tile_Z"));
    }

    public static ZiplineType getZiplineType(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains("zipline_type")) {
            return ZiplineType.STANDARD;
        }
        return ZiplineType.values()[tag.getByte("zipline_type") % ZiplineType.values().length];
    }

    public static void changeZiplineType(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putByte("zipline_type", (byte) ((getZiplineType(stack).ordinal() + 1) % ZiplineType.values().length));
    }

    public static void setZiplineType(ItemStack stack, ZiplineType type) {
        var tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        tag.putByte("zipline_type", (byte) type.ordinal());
    }
}
