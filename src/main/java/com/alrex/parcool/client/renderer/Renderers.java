package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.entity.EntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class Renderers {
    public static void register(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityTypes.ZIPLINE_ROPE.get(), ZiplineRopeRenderer::new);
    }
}
