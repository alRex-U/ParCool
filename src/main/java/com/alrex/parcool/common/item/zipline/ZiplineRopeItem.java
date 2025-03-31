package com.alrex.parcool.common.item.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ZiplineRopeItem extends Item {
    public ZiplineRopeItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> lines, ITooltipFlag flag) {
        CompoundNBT tag = stack.getTag();

        if (tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z")) {
            lines.add(new StringTextComponent("Position[" + tag.getInt("Tile_X") + "," + tag.getInt("Tile_Y") + "," + tag.getInt("Tile_Z") + "]").withStyle(TextFormatting.YELLOW));
        }
    }

    @Nonnull
    @Override
    public ActionResultType useOn(@Nonnull ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundNBT tag = stack.getTag();

        if (tag != null && tag.contains("Tile_X") && tag.contains("Tile_Y") && tag.contains("Tile_Z")) {
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ZiplineHookBlock) {
                BlockPos start = new BlockPos(tag.getInt("Tile_X"), tag.getInt("Tile_Y"), tag.getInt("Tile_Z"));
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
                }
                TileEntity startEntity = context.getLevel().getBlockEntity(start);
                TileEntity endEntity = context.getLevel().getBlockEntity(end);
                if (startEntity instanceof ZiplineHookTileEntity && endEntity instanceof ZiplineHookTileEntity) {
                    if (!context.getLevel().isClientSide()) {
                        ZiplineHookTileEntity startZipEntity = (ZiplineHookTileEntity) startEntity;
                        ZiplineHookTileEntity endZipEntity = (ZiplineHookTileEntity) endEntity;
                        startZipEntity.connectTo(endZipEntity);
                    }
                    tag.remove("Tile_X");
                    tag.remove("Tile_Y");
                    tag.remove("Tile_Z");
                    return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
                }
            }
            if (context.isSecondaryUseActive()) {
                tag.remove("Tile_X");
                tag.remove("Tile_Y");
                tag.remove("Tile_Z");
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        } else {
            BlockPos pos = context.getClickedPos();
            if (context.getLevel().getBlockState(pos).getBlock() instanceof ZiplineHookBlock) {
                if (tag == null) {
                    tag = new CompoundNBT();
                    stack.setTag(tag);
                }
                tag.putInt("Tile_X", pos.getX());
                tag.putInt("Tile_Y", pos.getY());
                tag.putInt("Tile_Z", pos.getZ());
                if (context.getLevel().isClientSide()) {
                    PlayerEntity player = context.getPlayer();
                    if (player != null) {
                        player.displayClientMessage(new StringTextComponent(pos.toString()), true);
                    }
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }
    }
}
