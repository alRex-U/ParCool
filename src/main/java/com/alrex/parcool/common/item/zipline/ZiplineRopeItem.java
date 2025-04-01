package com.alrex.parcool.common.item.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ZiplineRopeItem extends Item {
    public static class ItemColor implements IItemColor {
        @Override
        public int getColor(@Nonnull ItemStack itemStack, int i) {
            return i > 0 ? -1 : ZiplineRopeItem.getColor(itemStack);
        }
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
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> lines, ITooltipFlag flag) {
        CompoundNBT tag = stack.getTag();

        if (tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z")) {
            lines.add(new TranslationTextComponent("parcool.gui.text.zipline.bind_pos", tag.getInt("Tile_X") + ", " + tag.getInt("Tile_Y") + ", " + tag.getInt("Tile_Z")).withStyle(TextFormatting.YELLOW));
        }
        if (hasCustomColor(stack)) {
            int color = getColor(stack);
            DecimalFormat format = PERCENT_FORMATTER;
            float r = 100f * ((color & 0xFF0000) >> 16) / 255f;
            float g = 100f * ((color & 0x00FF00) >> 8) / 255f;
            float b = 100f * (color & 0x0000FF) / 255f;
            lines.add(new StringTextComponent(""));
            lines.add(new TranslationTextComponent("parcool.gui.text.zipline.color").withStyle(TextFormatting.GRAY));
            lines.add(new StringTextComponent("R : " + format.format(r) + "%").withStyle(TextFormatting.RED));
            lines.add(new StringTextComponent("G : " + format.format(g) + "%").withStyle(TextFormatting.GREEN));
            lines.add(new StringTextComponent("B : " + format.format(b) + "%").withStyle(TextFormatting.BLUE));
        }
    }

    @Nonnull
    @Override
    public ActionResultType useOn(@Nonnull ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundNBT tag = stack.getTag();

        // First Point is already registered
        if (tag != null && hasBlockPosition(stack)) {
            // Second Point is Found
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ZiplineHookBlock) {
                BlockPos start = getBlockPosition(stack);
                if (start == null) return ActionResultType.FAIL;

                BlockPos end = context.getClickedPos();
                if (start.equals(end)) return ActionResultType.PASS;
                if (start.distSqr(end) > Zipline.MAXIMUM_DISTANCE * Zipline.MAXIMUM_DISTANCE) {
                    if (context.getLevel().isClientSide()) {
                        PlayerEntity player = context.getPlayer();
                        if (player != null) {
                            player.displayClientMessage(new TranslationTextComponent("parcool.message.zipline.too_far"), true);
                        }
                    }
                    return ActionResultType.FAIL;
                } else if (Math.abs(end.getY() - start.getY()) * MathHelper.fastInvSqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getZ() - start.getZ(), 2)) > 1.) {
                    if (context.getLevel().isClientSide()) {
                        PlayerEntity player = context.getPlayer();
                        if (player != null) {
                            player.displayClientMessage(new TranslationTextComponent("parcool.message.zipline.too_steep"), true);
                        }
                    }
                    return ActionResultType.FAIL;
                }

                TileEntity startEntity = context.getLevel().getBlockEntity(start);
                TileEntity endEntity = context.getLevel().getBlockEntity(end);
                if (startEntity instanceof ZiplineHookTileEntity && endEntity instanceof ZiplineHookTileEntity) {
                    if (!context.getLevel().isClientSide()) {
                        ZiplineHookTileEntity startZipEntity = (ZiplineHookTileEntity) startEntity;
                        ZiplineHookTileEntity endZipEntity = (ZiplineHookTileEntity) endEntity;
                        startZipEntity.connectTo(endZipEntity, getColor(stack));
                    } else {
                        PlayerEntity player = context.getPlayer();
                        if (player != null) {
                            player.playSound(SoundEvents.ZIPLINE_SET.get(), 1, 1);
                        }
                    }
                    removeBlockPosition(stack);
                    stack.shrink(1);
                    return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
                }
            }
            // Remove position info
            if (context.isSecondaryUseActive()) {
                if (context.getLevel().isClientSide()) {
                    PlayerEntity player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(new TranslationTextComponent("parcool.message.zipline.reset_point"), true);
                    }
                }
                removeBlockPosition(stack);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        } else {
            BlockPos pos = context.getClickedPos();
            if (context.getLevel().getBlockState(pos).getBlock() instanceof ZiplineHookBlock) {
                setBlockPosition(stack, pos);
                if (context.getLevel().isClientSide()) {
                    PlayerEntity player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(new TranslationTextComponent("parcool.message.zipline.set_point", pos.toShortString()), true);
                    }
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }
    }

    public static void setBlockPosition(ItemStack stack, BlockPos pos) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.putInt("Tile_X", pos.getX());
        tag.putInt("Tile_Y", pos.getY());
        tag.putInt("Tile_Z", pos.getZ());
    }

    public static void removeBlockPosition(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return;
        }
        tag.remove("Tile_X");
        tag.remove("Tile_Y");
        tag.remove("Tile_Z");
    }

    public static boolean hasBlockPosition(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z");
    }

    @Nullable
    public static BlockPos getBlockPosition(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return null;
        }
        return new BlockPos(tag.getInt("Tile_X"), tag.getInt("Tile_Y"), tag.getInt("Tile_Z"));
    }

    public static void setColor(ItemStack stack, int color) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        tag.putInt("color", color);
    }

    public static int getColor(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains("color")) {
            return DEFAULT_COLOR;
        }
        return tag.getInt("color");
    }

    public static boolean hasCustomColor(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        return tag.contains("color");
    }
}
