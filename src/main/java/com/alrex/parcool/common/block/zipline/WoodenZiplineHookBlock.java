package com.alrex.parcool.common.block.zipline;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class WoodenZiplineHookBlock extends ZiplineHookBlock {
    protected static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];

    static {
        SHAPES[Direction.UP.ordinal()] = VoxelShapes.or(
                Block.box(6, 6, 6, 10, 8, 10),
                Block.box(5, 0, 5, 11, 6, 11)
        );
        SHAPES[Direction.DOWN.ordinal()] = VoxelShapes.or(
                Block.box(6, 8, 6, 10, 10, 10),
                Block.box(5, 10, 5, 11, 16, 11)
        );
        SHAPES[Direction.SOUTH.ordinal()] = VoxelShapes.or(
                Block.box(6, 6, 6, 10, 10, 8),
                Block.box(5, 5, 0, 11, 11, 6)
        );
        SHAPES[Direction.NORTH.ordinal()] = VoxelShapes.or(
                Block.box(6, 6, 8, 10, 10, 10),
                Block.box(5, 5, 10, 11, 11, 16)
        );
        SHAPES[Direction.WEST.ordinal()] = VoxelShapes.or(
                Block.box(8, 6, 6, 8, 10, 10),
                Block.box(10, 5, 5, 16, 11, 11)
        );
        SHAPES[Direction.EAST.ordinal()] = VoxelShapes.or(
                Block.box(6, 6, 6, 8, 10, 10),
                Block.box(0, 5, 5, 6, 11, 11)
        );
    }

    public WoodenZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }
}
