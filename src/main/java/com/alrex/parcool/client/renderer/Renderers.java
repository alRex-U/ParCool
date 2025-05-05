package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.entity.EntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class Renderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityTypes.ZIPLINE_ROPE.get(), ZiplineRopeRenderer::new);
    }
}
