package com.alrex.parcool.api.compatibility;

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
}
