package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.blockentity.ZiplineHookRenderer;
import com.alrex.parcool.common.block.TileEntities;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Renderers {
    public static void register() {
        BlockEntityRendererRegistry.register(TileEntities.ZIPLINE_HOOK.get(), ZiplineHookRenderer::new);
    }
}
