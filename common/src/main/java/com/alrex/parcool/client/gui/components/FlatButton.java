package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class FlatButton extends AbstractButton {
    private final int backColor;
    private final int txtColor;
    private final int shadowColor;
    private final boolean shadow;
    private final Font font;
    @Nullable
    private final Runnable runnable;

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, int shadowColor, boolean shadow) {
        this(font, x, y, width, text, backColor, txtColor, shadowColor, shadow, null);
    }

    public FlatButton(Font font, int x, int y, int width, Component text, int backColor, int txtColor, int shadowColor, boolean shadow, @Nullable Runnable runnable) {
        super(x, y, width, font.lineHeight + 8, text);
        this.font = font;
        this.backColor = backColor;
        this.txtColor = txtColor;
        this.shadowColor = shadowColor;
        this.shadow = shadow;
        this.runnable = runnable;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        renderFlatButton(poseStack, this.backColor, this.txtColor);
    }

    public void renderFlatButton(PoseStack poseStack, int backColor, int txtColor) {
        if (shadow && active) {
            fill(poseStack, x, y, x + width + 1, y + height + 1, shadowColor);
        }
        if (active) {
            if (isHovered) RenderSystem.setShaderColor(0.85f, 0.85f, 0.85f, 0.85f);
            else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        } else {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
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
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}
