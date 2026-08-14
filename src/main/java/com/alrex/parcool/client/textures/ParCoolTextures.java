package com.alrex.parcool.client.textures;

import com.alrex.parcool.api.action.ActionEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

@OnlyIn(Dist.CLIENT)
public class ParCoolTextures {
    private static ParCoolTextures INSTANCE;

    private final ParCoolActionsTextureAtlas actionsTextureAtlas;
    private final ParCoolGuiTextureAtlas guiTextureAtlas;
    private final ParCoolGuideImageTextureAtlas guideImageTextureAtlas;

    public static void init(RegisterClientReloadListenersEvent event) {
        INSTANCE = new ParCoolTextures(Minecraft.getInstance().textureManager);
        event.registerReloadListener(INSTANCE.actionsTextureAtlas);
        event.registerReloadListener(INSTANCE.guiTextureAtlas);
        event.registerReloadListener(INSTANCE.guideImageTextureAtlas);
    }

    public static ParCoolTextures instance() {
        return INSTANCE;
    }

    protected ParCoolTextures(TextureManager manager) {
        actionsTextureAtlas = new ParCoolActionsTextureAtlas(manager);
        guiTextureAtlas = new ParCoolGuiTextureAtlas(manager);
        guideImageTextureAtlas = new ParCoolGuideImageTextureAtlas(manager);
    }

    public static TextureAtlasSprite guiSprite(ResourceLocation spriteId) {
        return INSTANCE.guiTextureAtlas.getSprite(spriteId);
    }

    public static TextureAtlasSprite guideResourceSprite(ResourceLocation spriteId) {
        return INSTANCE.guideImageTextureAtlas.getSprite(spriteId);
    }

    public static TextureAtlasSprite action(ActionEntry<?> entry) {
        return INSTANCE.actionsTextureAtlas.getSprite(entry);
    }
}
