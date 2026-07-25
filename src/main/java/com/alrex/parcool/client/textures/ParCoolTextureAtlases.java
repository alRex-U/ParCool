package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

public class ParCoolTextureAtlases {
    private static ParCoolTextureAtlases INSTANCE;

    private final ParCoolActionsTextureAtlas actionsTextureAtlas;

    public static void init(RegisterClientReloadListenersEvent event) {
        INSTANCE = new ParCoolTextureAtlases(Minecraft.getInstance().textureManager, ParCool.getActionRegistry());
        event.registerReloadListener(INSTANCE.actionsTextureAtlas);
    }

    public static ParCoolTextureAtlases instance() {
        return INSTANCE;
    }

    protected ParCoolTextureAtlases(TextureManager manager, ActionRegistry actionRegistry) {
        actionsTextureAtlas = new ParCoolActionsTextureAtlas(manager, actionRegistry);
    }

    public TextureAtlasSprite getActionIcon(ActionEntry<?> entry) {
        return INSTANCE.actionsTextureAtlas.getSprite(entry);
    }

    public static TextureAtlasSprite action(ActionEntry<?> entry) {
        return INSTANCE.getActionIcon(entry);
    }
}
