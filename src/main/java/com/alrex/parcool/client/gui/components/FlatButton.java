package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FlatButton extends AbstractButton {
    private final int backColor;
    private final int txtColor;
    private final int shadowColor;
    private final boolean shadow;
    private final Font font;
    @Nullable
    private final Runnable runnable;

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, int shadowColor, boolean shadow) {
        this(font, x, y, width, text, backColor, txtColor, shadowColor, shadow, null);
    }

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, int shadowColor, boolean shadow, @Nullable Runnable runnable) {
        super(x, y, width, font.lineHeight + 8, text);
        this.font = font;
        this.backColor = backColor;
        this.txtColor = txtColor;
        this.shadowColor = shadowColor;
        this.shadow = shadow;
        this.runnable = runnable;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        renderFlatButton(graphics, this.backColor, this.txtColor);
    }

    public void renderFlatButton(@Nonnull GuiGraphics graphics, int backColor, int txtColor) {
        if (shadow && active) {
            graphics.fill(getX(), getY(), getX() + width + 1, getY() + height + 1, shadowColor);
        }
        if (active) {
            if (isHovered) RenderSystem.setShaderColor(0.85f, 0.85f, 0.85f, 0.85f);
            else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        } else {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        graphics.fill(getX(), getY(), getX() + width, getY() + height, backColor);
        var message = getMessage();
        graphics.drawString(font, message, (int) (getX() + (width - font.width(message)) / 2f), (int) (1 + getY() + (height - font.lineHeight) / 2f), txtColor, false);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onPress() {
        if (runnable != null) runnable.run();
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
