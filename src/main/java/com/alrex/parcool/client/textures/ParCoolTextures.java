package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
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

    public static void init(RegisterClientReloadListenersEvent event) {
        INSTANCE = new ParCoolTextures(Minecraft.getInstance().textureManager, ParCool.getActionRegistry());
        event.registerReloadListener(INSTANCE.actionsTextureAtlas);
        event.registerReloadListener(INSTANCE.guiTextureAtlas);
    }

    public static ParCoolTextures instance() {
        return INSTANCE;
    }

    protected ParCoolTextures(TextureManager manager, ActionRegistry actionRegistry) {
        actionsTextureAtlas = new ParCoolActionsTextureAtlas(manager, actionRegistry);
        guiTextureAtlas = new ParCoolGuiTextureAtlas(manager);
    }

    public TextureAtlasSprite getActionIcon(ActionEntry<?> entry) {
        return INSTANCE.actionsTextureAtlas.getSprite(entry);
    }

    public TextureAtlasSprite getGuiSprite(ResourceLocation spriteId) {
        return INSTANCE.guiTextureAtlas.getSprite(spriteId);
    }

    public static TextureAtlasSprite action(ActionEntry<?> entry) {
        return INSTANCE.getActionIcon(entry);
    }
}
