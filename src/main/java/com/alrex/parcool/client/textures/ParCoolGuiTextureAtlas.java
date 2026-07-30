package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

public class ParCoolGuiTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ParCool.MOD_ID, "textures/atlas/gui.png");
    private static final ArrayList<ResourceLocation> LOCATIONS = new ArrayList<>();

    public static final ResourceLocation STAMINA_FULL = register("hud/stamina_full");
    public static final ResourceLocation STAMINA_HALF = register("hud/stamina_half");
    public static final ResourceLocation STAMINA_EMPTY = register("hud/stamina_empty");
    public static final ResourceLocation STAMINA_EXHAUSTED_FULL = register("hud/stamina_exhausted_full");
    public static final ResourceLocation STAMINA_EXHAUSTED_HALF = register("hud/stamina_exhausted_half");
    public static final ResourceLocation STAMINA_EXHAUSTED_EMPTY = register("hud/stamina_exhausted_empty");
    public static final ResourceLocation STAMINA_INEXHAUSTIBLE_FULL = register("hud/stamina_inexhaustible_full");
    public static final ResourceLocation STAMINA_INEXHAUSTIBLE_HALF = register("hud/stamina_inexhaustible_half");
    public static final ResourceLocation STAMINA_INEXHAUSTIBLE_EMPTY = register("hud/stamina_inexhaustible_empty");
    public static final ResourceLocation STAMINA_BRIGHT_FULL = register("hud/stamina_bright_full");
    public static final ResourceLocation STAMINA_BRIGHT_HALF = register("hud/stamina_bright_half");
    public static final ResourceLocation STAMINA_BRIGHT_EMPTY = register("hud/stamina_bright_empty");
    public static final ResourceLocation STAMINA_FLUSH = register("hud/stamina_flush");

    public ParCoolGuiTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, "gui/sprites");
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getSprite(@Nonnull ResourceLocation spriteId) {
        return super.getSprite(spriteId);
    }

    @Nonnull
    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        LOCATIONS.trimToSize();
        return Collections.unmodifiableList(LOCATIONS).stream();
    }

    private static ResourceLocation register(String path) {
        var location = new ResourceLocation(ParCool.MOD_ID, path);
        LOCATIONS.add(location);
        return location;
    }
}
