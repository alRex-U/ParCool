package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import dev.architectury.registry.ReloadListenerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

@Environment(EnvType.CLIENT)
public class ParCoolTextures {
    private static ParCoolTextures INSTANCE;

    private final ParCoolActionsTextureAtlas actionsTextureAtlas;
    private final ParCoolGuiTextureAtlas guiTextureAtlas;
    private final ParCoolGuideImageTextureAtlas guideImageTextureAtlas;

    public static void init() {
        INSTANCE = new ParCoolTextures(Minecraft.getInstance().getTextureManager(), ParCool.getActionRegistry());
        ReloadListenerRegistry.register(
                PackType.CLIENT_RESOURCES, INSTANCE.actionsTextureAtlas
        );
        ReloadListenerRegistry.register(
                PackType.CLIENT_RESOURCES, INSTANCE.guiTextureAtlas
        );
        ReloadListenerRegistry.register(
                PackType.CLIENT_RESOURCES, INSTANCE.guideImageTextureAtlas
        );
    }

    public static ParCoolTextures instance() {
        return INSTANCE;
    }

    protected ParCoolTextures(TextureManager manager, ActionRegistry actionRegistry) {
        actionsTextureAtlas = new ParCoolActionsTextureAtlas(manager, actionRegistry);
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
