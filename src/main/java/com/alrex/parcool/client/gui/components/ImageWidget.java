package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImageWidget extends AbstractWidget {
    @Nullable
    private TextureAtlasSprite sprite;

    public ImageWidget(int x, int y, int width, int height, @Nullable TextureAtlasSprite sprite) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (sprite == null) return;
        blit(poseStack, x, y, 0, width, height, sprite);
    }

    public void setImage(@Nullable TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
