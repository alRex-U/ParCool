package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.common.block.BlockStateProperties;
import com.alrex.parcool.common.block.TileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ZiplineHookBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    public Vec3 getActualZiplinePoint(BlockPos pos, BlockState state) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING).add(POWERED);
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(@Nonnull BlockState p_149656_1_) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (!state.is(newState.getBlock())) {
            if (!world.isClientSide()) {
                var tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof ZiplineHookTileEntity ziplineHookTileEntity) {
                    List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnections();
                    itemStacks.forEach((it) -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), it));
                }
            }
            super.onRemove(state, world, pos, newState, p_196243_5_);
        }
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, @Nonnull Direction direction, @Nonnull BlockState state1, @Nonnull LevelAccessor levelAccessor, @Nonnull BlockPos pos, @Nonnull BlockPos pos1) {
        Direction facing = state.getValue(FACING);
        return direction == facing.getOpposite() && !canSurvive(state, levelAccessor, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, levelAccessor, pos, pos1);
    }

    @Override
    public void neighborChanged(
            @Nonnull BlockState state,
            @Nonnull Level level,
            @Nonnull BlockPos pos,
            @Nonnull Block cause,
            @Nonnull BlockPos changedPos,
            boolean p_60514_
    ) {
        if (!canSurvive(state, level, pos)) return;
        Direction facing = state.getValue(FACING);
        BlockPos supportingBlock = pos.relative(facing.getOpposite());
        if (supportingBlock.equals(changedPos)) {
            var powered = state.getValue(POWERED);
            var signal = level.getSignal(changedPos, facing.getOpposite());
            if (signal > 0) {
                if (!powered) level.setBlock(pos, state.setValue(POWERED, true), 3);
            } else {
                if (powered) level.setBlock(pos, state.setValue(POWERED, false), 3);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource randomSource) {
        if (state.getValue(POWERED) && randomSource.nextInt(7) == 0) {
            if (level.getBlockEntity(pos) instanceof ZiplineHookTileEntity hook) {
                var hookPos = hook.getHookPoint();
                level.addParticle(DustParticleOptions.REDSTONE, hookPos.x, hookPos.y, hookPos.z, 0.0, randomSource.nextFloat() * 0.1, 0.0);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, @Nonnull LevelReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportingBlock = pos.relative(facing.getOpposite());
        return canSupportCenter(world, supportingBlock, facing);
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState p_60475_, @Nonnull BlockGetter p_60476_, @Nonnull BlockPos p_60477_, @Nonnull PathComputationType p_60478_) {
        return false;
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState blockState, @Nonnull Level world, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult blockRayTraceResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof ShearsItem) {
            var tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ZiplineHookTileEntity ziplineHookTileEntity) {
                if (ziplineHookTileEntity.getConnectionPoints().isEmpty()) return InteractionResult.PASS;

                List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnections();
                if (!itemStacks.isEmpty()) {
                    player.playSound(ParCoolSoundEvents.ZIPLINE_REMOVE.get(), 1, 1);
                }
                if (world.isClientSide()) {
                    return InteractionResult.SUCCESS;
                } else {
                    itemStacks.forEach((it) -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), it));
                    if (!itemStacks.isEmpty()) {
                        if (stack.isDamageableItem()) {
                            stack.hurtAndBreak(1, player, (it) -> {
                            });
                        }
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new ZiplineHookTileEntity(TileEntities.ZIPLINE_HOOK.get(), blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) return null;
        return type == TileEntities.ZIPLINE_HOOK.get() ? ZiplineHookTileEntity::tick : null;
    }
}
