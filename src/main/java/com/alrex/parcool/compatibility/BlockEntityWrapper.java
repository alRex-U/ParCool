package com.alrex.parcool.compatibility;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;

import net.minecraft.tileentity.TileEntity;

public class BlockEntityWrapper {
    private TileEntity blockEntity;

    public BlockEntityWrapper(TileEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @SuppressWarnings("unchecked")
    public <T extends TileEntity> T cast(Class<T> classTarget) {
        return classTarget.isInstance(blockEntity) ? (T) blockEntity : null;
    }

    public boolean is(Class<ZiplineHookTileEntity> class1) {
        return class1.isInstance(blockEntity);
    }

    public TileEntity getInstance() {
        return blockEntity;
    }
}
