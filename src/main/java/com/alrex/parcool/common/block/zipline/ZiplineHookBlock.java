package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.TileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ZiplineHookBlock extends Block {
    protected static final VoxelShape SHAPE;

    static {
        SHAPE = VoxelShapes.or(
                Block.box(6, 8, 6, 10, 16, 10),
                Block.box(5, 10, 5, 11, 14, 11)
        );
    }

    public ZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState p_196243_4_, boolean p_196243_5_) {
        if (!world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ZiplineHookTileEntity) {
                ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity) tileEntity;
                List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnection();
                itemStacks.forEach((it) -> InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), it));
            }
        }
        super.onRemove(state, world, pos, p_196243_4_, p_196243_5_);
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ShearsItem) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ZiplineHookTileEntity) {
                ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity) tileEntity;
                if (ziplineHookTileEntity.getConnectionPoints().isEmpty()) return ActionResultType.PASS;

                List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnection();
                if (world.isClientSide()) {
                    player.playSound(SoundEvents.ZIPLINE_REMOVE.get(), 1, 1);
                    return ActionResultType.SUCCESS;
                } else {
                    itemStacks.forEach((it) -> InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), it));
                    if (!itemStacks.isEmpty()) {
                        if (stack.isDamageableItem()) {
                            stack.setDamageValue(stack.getDamageValue() + 1);
                        }
                    }
                    return ActionResultType.CONSUME;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get());
    }

}
