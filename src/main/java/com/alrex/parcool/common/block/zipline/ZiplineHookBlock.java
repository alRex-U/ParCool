package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.common.block.TileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ZiplineHookBlock extends DirectionalBlock {

    public ZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    public Vec3Wrapper getActualZiplinePoint(BlockPos pos, BlockState state) {
        return new Vec3Wrapper(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
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
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos pos1) {
        Direction facing = state.getValue(FACING);
        return direction == facing.getOpposite() && !canSurvive(state, world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, world, pos, pos1);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportingBlock = pos.relative(facing.getOpposite());
        return canSupportCenter(world, supportingBlock, facing);
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        PlayerWrapper player = PlayerWrapper.get(playerEntity);
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ShearsItem) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ZiplineHookTileEntity) {
                ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity) tileEntity;
                if (ziplineHookTileEntity.getConnectionPoints().isEmpty()) return ActionResultType.PASS;

                List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnection();
                player.playSound(SoundEvents.ZIPLINE_REMOVE.get(), 1, 1);
                if (world.isClientSide()) {
                    return ActionResultType.SUCCESS;
                } else {
                    itemStacks.forEach((it) -> InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), it));
                    if (!itemStacks.isEmpty() && stack.isDamageableItem()) {
                        player.hurtAndBreakStack(1, stack);
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
