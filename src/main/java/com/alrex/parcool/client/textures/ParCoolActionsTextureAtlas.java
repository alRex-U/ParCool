package com.alrex.parcool.client.textures;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParCoolActionsTextureAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/atlas/action_icons.png");

    protected ParCoolActionsTextureAtlas(TextureManager manager) {
        super(manager, TEXTURE_LOCATION, ParCool.resourceLocation("parcool_actions"));
    }

    public TextureAtlasSprite getSprite(ActionEntry<?> entry) {
        return getSprite(entry.id());
    }
}
