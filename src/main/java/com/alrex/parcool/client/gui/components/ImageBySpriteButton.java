package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImageBySpriteButton extends AbstractButton {
    private final int txtColor;
    private final Font font;
    @Nullable
    private final Runnable runnable;
    private final ResourceLocation texLocation;
    private final TextureAtlasSprite sprite;

    public ImageBySpriteButton(Font font, int x, int y, int width, int height, Component text, int txtColor, ResourceLocation texLocation, TextureAtlasSprite sprite, @Nullable Runnable onPressListener) {
        super(x, y, width, height, text);
        this.font = font;
        this.txtColor = txtColor;
        this.runnable = onPressListener;
        this.texLocation = texLocation;
        this.sprite = sprite;
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (active) {
            if (isHovered) RenderSystem.setShaderColor(0.85f, 0.85f, 0.85f, 0.85f);
            else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        } else {
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        RenderSystem.setShaderTexture(0, texLocation);
        blit(poseStack, x, y, 0, width, height, sprite);
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
