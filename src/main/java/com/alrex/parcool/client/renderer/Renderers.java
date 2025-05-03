package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.entity.ParcoolEntityType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Renderers {
    public static void register(EntityRendererManager manager) {
        manager.register(ParcoolEntityType.ZIPLINE_ROPE.get(), new ZiplineRopeRenderer(manager));
    }
}
