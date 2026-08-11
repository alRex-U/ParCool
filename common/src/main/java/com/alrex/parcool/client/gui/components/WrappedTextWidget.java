package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

@Environment(EnvType.CLIENT)
public class WrappedTextWidget extends TextWidget {
    private List<FormattedCharSequence> sequences;

    public WrappedTextWidget(Font font, int x, int y, int width, Component message, HorizontalAlignment textAlignment, int txtColor) {
        super(font, x, y, width, message, textAlignment, txtColor);
        this.sequences = font.split(message, width);
        height = font.lineHeight * sequences.size();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.sequences = font.split(getMessage(), width);
        height = font.lineHeight * sequences.size();
    }

    @Override
    public void setMessage(Component message) {
        super.setMessage(message);
        this.sequences = font.split(message, width);
        height = font.lineHeight * sequences.size();
    }

    public WrappedTextWidget withShadow(boolean value) {
        this.shadow = value;
        return this;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var iterator = sequences.iterator();
        int textY = y;
        while (iterator.hasNext()) {
            var text = iterator.next();
            var xOffset = switch (alignment) {
                case START -> x;
                case END -> x + width - font.width(text);
                case CENTER -> x + (width - font.width(text)) / 2f;
            };
            if (shadow) {
                font.drawShadow(poseStack, text, xOffset, textY, txtColor);
            } else {
                font.draw(poseStack, text, xOffset, textY, txtColor);
            }
            textY += font.lineHeight;
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        super.updateNarration(narrationElementOutput);
    }
}
