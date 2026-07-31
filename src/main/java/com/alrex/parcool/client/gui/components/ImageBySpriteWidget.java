package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ImageBySpriteWidget extends AbstractWidget {
    @Nullable
    private TextureAtlasSprite sprite;

    public ImageBySpriteWidget(int x, int y, int width, int height, @Nullable TextureAtlasSprite sprite) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        if (sprite == null) return;
        blit(poseStack, x, y, 0, width, height, sprite);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    public void setImage(@Nullable TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
