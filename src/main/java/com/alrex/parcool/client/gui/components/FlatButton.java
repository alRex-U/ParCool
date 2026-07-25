package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlatButton extends AbstractButton {
    private final int backColor;
    private final int txtColor;
    private final boolean shadow;
    private final Font font;
    @Nullable
    private final Runnable runnable;

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, boolean shadow) {
        this(font, x, y, width, text, backColor, txtColor, shadow, null);
    }

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, boolean shadow, @Nullable Runnable runnable) {
        super(x, y, width, font.lineHeight + 8, text);
        this.font = font;
        this.backColor = backColor;
        this.txtColor = txtColor;
        this.shadow = shadow;
        this.runnable = runnable;
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        renderFlatButton(poseStack, mouseX, mouseY, partial, this.backColor, this.txtColor);
    }

    public void renderFlatButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial, int backColor, int txtColor) {
        if (isHovered) RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        else RenderSystem.setShaderColor(0.9375f, 0.9375f, 0.9375f, 0.9375f);
        if (shadow && active) {
            fill(poseStack, x, y, x + width + 1, y + height + 1, 0x55000000);
        }
        fill(poseStack, x, y, x + width, y + height, backColor);
        var message = getMessage();
        font.draw(poseStack, message, x + (width - font.width(message)) / 2f, 1 + y + (height - font.lineHeight) / 2f, txtColor);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onPress() {
        if (runnable != null) runnable.run();
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
