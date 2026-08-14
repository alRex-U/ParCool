package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TextWidget extends AbstractWidget {
    public enum HorizontalAlignment {
        START, CENTER, END
    }

    protected final Font font;
    protected final int txtColor;
    protected boolean shadow;
    protected final HorizontalAlignment alignment;
    private int messageWidth;

    public TextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment alignment, int txtColor) {
        super(x, y, width, font.lineHeight, message);
        this.font = font;
        this.txtColor = txtColor;
        this.alignment = alignment;
        this.messageWidth = font.width(getMessage());
    }

    public TextWidget withShadow(boolean value) {
        this.shadow = value;
        return this;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var xOffset = switch (alignment) {
            case START -> getX();
            case END -> getX() + width - messageWidth;
            case CENTER -> getX() + (width - messageWidth) / 2f;
        };
        graphics.drawString(font, getMessage(), (int) xOffset, getY(), txtColor, shadow);
    }

    @Override
    public void setMessage(@Nonnull Component message) {
        super.setMessage(message);
        messageWidth = font.width(getMessage());
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
