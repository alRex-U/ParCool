package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class ParCoolGuideImageTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/parcool_guide_img.png");

    public ParCoolGuideImageTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, "parcool_guide");
    }

    @Nonnull
    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        var resources = Minecraft.getInstance().getResourceManager().listResources("textures/parcool_guide", (location) -> true);
        return resources.keySet().stream().map(it -> {
            var path = it.getPath();
            return path.startsWith("textures/parcool_guide/") && path.endsWith(".png")
                    ? new ResourceLocation(it.getNamespace(), path.substring(23, path.length() - 4))
                    : new ResourceLocation(it.getNamespace(), path);
        });
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getSprite(@Nonnull ResourceLocation id) {
        return super.getSprite(id);
    }
}
