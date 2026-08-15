package com.alrex.parcool.common.block.zipline;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class WoodenZiplineHookBlock extends ZiplineHookBlock {
    protected static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];

    static {
        SHAPES[Direction.UP.ordinal()] = Shapes.or(
                Block.box(6, 6, 6, 10, 8, 10),
                Block.box(5, 0, 5, 11, 6, 11)
        );
        SHAPES[Direction.DOWN.ordinal()] = Shapes.or(
                Block.box(6, 8, 6, 10, 10, 10),
                Block.box(5, 10, 5, 11, 16, 11)
        );
        SHAPES[Direction.SOUTH.ordinal()] = Shapes.or(
                Block.box(6, 6, 6, 10, 10, 8),
                Block.box(5, 5, 0, 11, 11, 6)
        );
        SHAPES[Direction.NORTH.ordinal()] = Shapes.or(
                Block.box(6, 6, 8, 10, 10, 10),
                Block.box(5, 5, 10, 11, 11, 16)
        );
        SHAPES[Direction.WEST.ordinal()] = Shapes.or(
                Block.box(8, 6, 6, 8, 10, 10),
                Block.box(10, 5, 5, 16, 11, 11)
        );
        SHAPES[Direction.EAST.ordinal()] = Shapes.or(
                Block.box(6, 6, 6, 8, 10, 10),
                Block.box(0, 5, 5, 6, 11, 11)
        );
    }

    private static final MapCodec<WoodenZiplineHookBlock> CODEC = simpleCodec(WoodenZiplineHookBlock::new);

    @Nonnull
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public WoodenZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }
}
