package com.alrex.parcool.client.renderer;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes {
    public static final RenderType ZIPLINE;

    static {
        ZIPLINE = RenderType.create(
                "zipline", 3000,
                RenderPipelines.LEASH,
                RenderType.CompositeState.builder()
                        .setTextureState(RenderStateShard.NO_TEXTURE)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false)
        );
    }
}
