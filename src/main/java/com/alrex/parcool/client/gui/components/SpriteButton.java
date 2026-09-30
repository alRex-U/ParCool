package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpriteButton extends AbstractSpriteButton {
    @Nullable
    private Runnable runnable;

    public SpriteButton(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component text,
            int txtColor,
            TextureAtlasSprite spriteWhenOn,
            TextureAtlasSprite spriteWhenOnHover,
            TextureAtlasSprite spriteWhenInactive,
            @Nullable Runnable onPressListener
    ) {
        super(font, x, y, width, height, text, txtColor, spriteWhenOn, spriteWhenOnHover, spriteWhenInactive);
        runnable = onPressListener;
    }

    @Override
    public void onPress() {
        if (runnable != null) runnable.run();
    }

    public void setPressedListener(@Nullable Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        var sprite = active
                ? (isHovered ? spriteWhenOnHover : spriteWhenOn)
                : spriteWhenInactive;
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        blit(poseStack, x, y, 0, width, height, sprite);
        renderInnerMessage(poseStack, getMessage());
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
