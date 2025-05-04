package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class IronZiplineHookBlock extends ZiplineHookBlock {
    public static final BooleanProperty ORTHOGONAL = BlockStateProperties.ORTHOGONAL;
    protected static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];

    static {
        SHAPES[Direction.DOWN.ordinal()] = Block.box(
                6, 11, 6, 10, 16, 10
        );
        SHAPES[Direction.UP.ordinal()] = Block.box(
                6, 0, 6, 10, 5, 10
        );
        SHAPES[Direction.SOUTH.ordinal()] = Block.box(
                6, 6, 0, 10, 10, 5
        );
        SHAPES[Direction.NORTH.ordinal()] = Block.box(
                6, 6, 11, 10, 10, 16
        );
        SHAPES[Direction.WEST.ordinal()] = Block.box(
                11, 6, 6, 16, 10, 10
        );
        SHAPES[Direction.EAST.ordinal()] = Block.box(
                0, 6, 6, 5, 10, 10
        );
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }

    public IronZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(ORTHOGONAL, false));
    }

    @Override
    public Vec3 getActualZiplinePoint(BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        return new Vec3(
                pos.getX() + 0.5 - direction.getStepX() * 0.2,
                pos.getY() + 0.5 - direction.getStepY() * 0.2,
                pos.getZ() + 0.5 - direction.getStepZ() * 0.2
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(ORTHOGONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        boolean orthogonal;
        var player = context.getPlayer();
        if (player == null)
            orthogonal = false;
        else {
            var lookVec = player.getLookAngle();
            switch (context.getClickedFace()) {
                case NORTH:
                case SOUTH:
                    orthogonal = Math.abs(lookVec.y()) < Math.abs(lookVec.x());
                    break;
                case EAST:
                case WEST:
                    orthogonal = Math.abs(lookVec.y()) < Math.abs(lookVec.z());
                    break;
                case UP:
                case DOWN:
                default:
                    orthogonal = Math.abs(lookVec.z()) < Math.abs(lookVec.x());
                    break;
            }
        }
        return state.setValue(ORTHOGONAL, orthogonal);
    }
}
