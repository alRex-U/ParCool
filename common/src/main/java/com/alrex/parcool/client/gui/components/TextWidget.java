package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var xOffset = switch (alignment) {
            case START -> x;
            case END -> x + width - messageWidth;
            case CENTER -> x + (width - messageWidth) / 2f;
        };
        if (shadow) {
            font.drawShadow(poseStack, getMessage(), xOffset, y, txtColor);
        } else {
            font.draw(poseStack, getMessage(), xOffset, y, txtColor);
        }
    }

    @Override
    public void setMessage(Component message) {
        super.setMessage(message);
        messageWidth = font.width(getMessage());
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
