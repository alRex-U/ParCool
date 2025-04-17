package com.alrex.parcool.client.renderer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class RenderTypes {
    public static final RenderType ZIPLINE_3D;

    static {
        ZIPLINE_3D = RenderType.create(
                "zipline2d",
                DefaultVertexFormats.POSITION_COLOR_LIGHTMAP,
                GL11.GL_QUADS, 256,
                RenderType.State.builder()
                        .setTextureState(RenderState.NO_TEXTURE)
                        .setCullState(RenderState.CULL)
                        .setLightmapState(RenderState.LIGHTMAP)
                        .createCompositeState(false)
        );
    }
}
