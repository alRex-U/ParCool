package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

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
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }

    public IronZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(defaultBlockState().setValue(ORTHOGONAL, false));
    }

    @Override
    public Vector3d getActualZiplinePoint(BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        return new Vector3d(
                pos.getX() + 0.5 - direction.getStepX() * 0.2,
                pos.getY() + 0.5 - direction.getStepY() * 0.2,
                pos.getZ() + 0.5 - direction.getStepZ() * 0.2
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(ORTHOGONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);

        boolean orthogonal;
        PlayerEntity player = context.getPlayer();
        if (player == null)
            orthogonal = false;
        else {
            Vector3d lookVec = player.getLookAngle();
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
