package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.TileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZiplineHookBlock extends Block {
    public ZiplineHookBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }


    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState p_196243_4_, boolean p_196243_5_) {
        if (!world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ZiplineHookTileEntity) {
                ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity) tileEntity;
                ziplineHookTileEntity.removeAllConnection();
            }
        }
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
