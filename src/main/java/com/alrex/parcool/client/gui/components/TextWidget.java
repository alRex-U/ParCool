package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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

    private final Font font;
    private final int txtColor;
    private final boolean shadow;
    private final HorizontalAlignment alignment;
    private int messageWidth;

    public TextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment alignment, int txtColor) {
        this(font, x, y, width, message, alignment, txtColor, false);
    }

    public TextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment alignment, int txtColor, boolean shadow) {
        super(x, y, width, font.lineHeight, message);
        this.font = font;
        this.txtColor = txtColor;
        this.alignment = alignment;
        this.messageWidth = font.width(getMessage());
        this.shadow = shadow;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
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
    public void setMessage(@Nonnull Component message) {
        super.setMessage(message);
        messageWidth = font.width(getMessage());
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {

    }
}
