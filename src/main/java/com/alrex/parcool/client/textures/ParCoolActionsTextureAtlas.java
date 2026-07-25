package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ParCoolActionsTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ParCool.MOD_ID, "textures/atlas/action_icons.png");

    private final TreeSet<ResourceLocation> registeredLocations = new TreeSet<>();

    protected ParCoolActionsTextureAtlas(TextureManager manager, ActionRegistry actionRegistry) {
        super(manager, TEXTURE_LOCATION, "parcool_actions");
        for (var action : actionRegistry.getRegisteredActions().entrySet()) {
            registeredLocations.add(action.getValue().id());
        }
    }

    public TextureAtlasSprite getSprite(ActionEntry<?> entry) {
        return getSprite(entry.id());
    }

    @Nonnull
    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        return Collections.unmodifiableSet(registeredLocations).stream();
    }
}
