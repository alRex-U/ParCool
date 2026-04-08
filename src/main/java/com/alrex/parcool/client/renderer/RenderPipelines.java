package com.alrex.parcool.client.renderer;

import com.alrex.parcool.ParCool;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;

public class RenderPipelines {
    private static final RenderPipeline.Snippet ZIPLINE_SNIPPET = RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.MATRICES_FOG_SNIPPET)
        .withVertexShader("core/rendertype_leash")
        .withFragmentShader("core/rendertype_leash")
        .withSampler("Sampler2")
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS)
        .buildSnippet();

    public static final RenderPipeline ZIPLINE_3D = RenderPipeline.builder(ZIPLINE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline3d"))
        .withCull(true)
        .build();
    public static final RenderPipeline ZIPLINE_2D = RenderPipeline.builder(ZIPLINE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline2d"))
        .withCull(false)
        .build();
}
