package com.alrex.parcool.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RenderTypes {
    public static final RenderType ZIPLINE_3D;
    public static final RenderType ZIPLINE_2D;

    static {
        ZIPLINE_2D = RenderType.create(
                "zipline2d",
                RenderSetup.builder(RenderPipelines.ZIPLINE_2D)
                        .useLightmap()
                        .bufferSize(256)
                        .createRenderSetup()
        );
        ZIPLINE_3D = RenderType.create(
                "zipline3d",
                RenderSetup.builder(RenderPipelines.ZIPLINE_2D)
                        .useLightmap()
                        .bufferSize(256)
                        .createRenderSetup()
        );
    }
}
