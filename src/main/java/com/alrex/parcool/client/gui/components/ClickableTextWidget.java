package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class ClickableTextWidget extends TextWidget {
    private final int hoveredTxtColor;
    private final Runnable pressListener;

    public ClickableTextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment alignment, int txtColor, int hoveredTxtColor, Runnable pressListener) {
        super(font, x, y, width, message, alignment, txtColor);
        this.hoveredTxtColor = hoveredTxtColor;
        this.pressListener = pressListener;
    }

    @Override
    protected boolean isValidClickButton(int click) {
        return true;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        var xOffset = switch (alignment) {
            case START -> getX();
            case END -> getX() + width - messageWidth;
            case CENTER -> getX() + (width - messageWidth) / 2f;
        };
        graphics.drawString(font, getMessage(), (int) xOffset, getY(), isHovered ? hoveredTxtColor : txtColor, shadow);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (pressListener != null) pressListener.run();
    }
}
