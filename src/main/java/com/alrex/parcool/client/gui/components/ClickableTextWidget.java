package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        var xOffset = switch (alignment) {
            case START -> x;
            case END -> x + width - messageWidth;
            case CENTER -> x + (width - messageWidth) / 2f;
        };
        if (shadow) {
            font.drawShadow(poseStack, getMessage(), xOffset, y, isHovered ? hoveredTxtColor : txtColor);
        } else {
            font.draw(poseStack, getMessage(), xOffset, y, isHovered ? hoveredTxtColor : txtColor);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (pressListener != null) pressListener.run();
    }
}
