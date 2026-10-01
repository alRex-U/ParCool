package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.util.ColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    public CardPanel shadowAll(boolean value) {
        shadowTop = shadowBottom = shadowLeft = shadowRight = value;
        return this;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        renderShadow(graphics);
        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);
    }

    protected void renderShadow(GuiGraphics graphics) {
        if (shadowRight || shadowBottom || shadowTop || shadowLeft) {
            if (ColorUtil.alphaOf(color) == 0xFF) {
                graphics.fill(
                        getX() + (shadowLeft ? -1 : 0),
                        getY() + (shadowTop ? -1 : 0),
                        getX() + width + (shadowRight ? 1 : 0),
                        getY() + height + (shadowBottom ? 1 : 0),
                        shadowColor
                );
            } else {
                if (shadowTop) graphics.fill(getX(), getY() - 1, getX() + width, getY(), shadowColor);
                if (shadowBottom) graphics.fill(getX(), getY() + height, getX() + width, getY() + height + 1, shadowColor);
                if (shadowLeft) graphics.fill(getX() - 1, getY(), getX(), getY() + height, shadowColor);
                if (shadowRight) graphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, shadowColor);
            }
        }
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
