package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        renderShadow(poseStack);
        fill(poseStack, x, y, x + width, y + height, color);
    }

    protected void renderShadow(PoseStack poseStack) {
        if (shadowRight || shadowBottom || shadowTop || shadowLeft) {
            if (ColorUtil.alphaOf(color) == 0xFF) {
                fill(poseStack,
                        x + (shadowLeft ? -1 : 0),
                        y + (shadowTop ? -1 : 0),
                        x + width + (shadowRight ? 1 : 0),
                        y + height + (shadowBottom ? 1 : 0),
                        shadowColor
                );
            } else {
                if (shadowTop) fill(poseStack, x, y - 1, x + width, y, shadowColor);
                if (shadowBottom) fill(poseStack, x, y + height, x + width, y + height + 1, shadowColor);
                if (shadowLeft) fill(poseStack, x - 1, y, x, y + height, shadowColor);
                if (shadowRight) fill(poseStack, x + width, y, x + width + 1, y + height, shadowColor);
            }
        }
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
