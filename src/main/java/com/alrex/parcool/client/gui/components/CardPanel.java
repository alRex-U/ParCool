package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class CardPanel extends AbstractWidget {
    private final int color;
    private final int shadowColor;
    private final boolean shadow;

    public CardPanel(int x, int y, int width, int height, int color, int shadowColor) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.shadowColor = shadowColor;
        this.shadow = true;
    }

    public CardPanel(int x, int y, int width, int height, int color) {
        super(x, y, width, height, Component.empty());
        this.color = color;
        this.shadowColor = 0;
        this.shadow = false;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (shadow) {
            fill(poseStack, x, y, x + width + 1, y + height + 1, shadowColor);
        }
        fill(poseStack, x, y, x + width, y + height, color);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
