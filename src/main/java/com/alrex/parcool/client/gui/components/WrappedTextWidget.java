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
public class WrappedTextWidget extends AbstractWidget {
    private final Font font;
    private final int txtColor;

    public WrappedTextWidget(Font font, int x, int y, int width, int height, Component message, int txtColor) {
        super(x, y, width, height, message);
        this.font = font;
        this.txtColor = txtColor;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        font.drawWordWrap(getMessage(), x, y, width, txtColor);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {

    }
}
