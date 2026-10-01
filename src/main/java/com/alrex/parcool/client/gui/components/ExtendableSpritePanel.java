package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class ExtendableSpritePanel extends AbstractWidget {
    protected final TextureAtlasSprite sprite;

    public ExtendableSpritePanel(
            int x,
            int y,
            int width,
            int height,
            TextureAtlasSprite sprite
    ) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        GuiRenderUtil.renderExtendableSprite(poseStack, sprite, x, y, width, height);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
