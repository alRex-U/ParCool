package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;

public class ZiplineRopeRenderState extends EntityRenderState {
    public BlockPos startPos;
    public BlockPos endPos;
    public int color;
    public Zipline zipline;
    int startBlockLightLevel;
    int endBlockLightLevel;
    int startSkyBrightness;
    int endSkyBrightness;
}