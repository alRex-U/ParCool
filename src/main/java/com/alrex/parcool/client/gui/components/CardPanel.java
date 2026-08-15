package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CardPanel extends AbstractWidget {
    private final int color;
    private final int shadowColor;
    private boolean shadowLeft;
    private boolean shadowRight;
    private boolean shadowTop;
    private boolean shadowBottom;

    public CardPanel(int x, int y, int width, int height, int color, int shadowColor) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.shadowColor = shadowColor;
        shadowRight = shadowBottom = true;
    }

    public CardPanel(int x, int y, int width, int height, int color) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.shadowColor = 0;
    }


    public CardPanel shadowRight(boolean value) {
        this.shadowRight = value;
        return this;
    }

    public CardPanel shadowLeft(boolean value) {
        this.shadowLeft = value;
        return this;
    }

    public CardPanel shadowTop(boolean value) {
        this.shadowTop = value;
        return this;
    }

    public CardPanel shadowBottom(boolean value) {
        this.shadowBottom = value;
        return this;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        if (shadowRight || shadowBottom || shadowTop || shadowLeft) {
            graphics.fill(
                    getX() + (shadowLeft ? -1 : 0),
                    getY() + (shadowTop ? -1 : 0),
                    getX() + width + (shadowRight ? 1 : 0),
                    getY() + height + (shadowBottom ? 1 : 0),
                    shadowColor
            );
        }
        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
