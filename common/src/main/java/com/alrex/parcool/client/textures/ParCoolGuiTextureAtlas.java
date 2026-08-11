package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ParCoolGuiTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/gui.png");
    private static final ArrayList<ResourceLocation> LOCATIONS = new ArrayList<>();

    public static final ResourceLocation BREWING_RECIPE_BOX = register("icon/brewing_recipe_box");
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
    public static final ResourceLocation SKILLTREE_ACTION_UNAVAILABLE = register("icon/action_frame_unavailable");
    public static final ResourceLocation SKILLTREE_ACTION_UNLOCKED = register("icon/action_frame_unlocked");
    public static final ResourceLocation SKILLTREE_ACTION_LOCKED = register("icon/action_frame_locked");
    public static final ResourceLocation BASIC_BUTTON = register("icon/basic_button");
    public static final ResourceLocation TOGGLE_BUTTON_ON = register("icon/toggle_button_on");
    public static final ResourceLocation TOGGLE_BUTTON_OFF = register("icon/toggle_button_off");
    public static final ResourceLocation UNLOCK_COST_BOX = register("icon/unlock_cost_box");
    public static final ResourceLocation EXPERIENCE_BOX = register("icon/experience_box");
    public static final ResourceLocation ICON_EXPERIENCE = register("icon/icon_experience");
    public static final ResourceLocation BUTTON_BACK = register("icon/button_back");
    public static final ResourceLocation BUTTON_CLOSE = register("icon/button_close");
    public static final ResourceLocation BUTTON_HAMBURGER = register("icon/button_hamburger");
    public static final ResourceLocation BUTTON_HOME = register("icon/button_home");

    public ParCoolGuiTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, "gui/sprites");
    }


    @Override
    public TextureAtlasSprite getSprite(ResourceLocation spriteId) {
        return super.getSprite(spriteId);
    }


    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        LOCATIONS.trimToSize();
        return Collections.unmodifiableList(LOCATIONS).stream();
    }

    private static ResourceLocation register(String path) {
        var location = ParCool.resourceLocation(path);
        LOCATIONS.add(location);
        return location;
    }
}
