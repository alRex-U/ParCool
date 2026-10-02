package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        GuiRenderUtil.renderExtendableSprite(graphics, sprite, getX(), getY(), width, height);
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
