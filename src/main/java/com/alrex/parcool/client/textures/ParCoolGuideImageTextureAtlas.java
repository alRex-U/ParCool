package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ParCoolGuideImageTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/parcool_guide_img.png");

    public ParCoolGuideImageTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, ParCool.resourceLocation("parcool_guide"));
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getSprite(@Nonnull ResourceLocation id) {
        return super.getSprite(id);
    }
}
