package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendableSpriteButton extends AbstractExtendableSpriteButton {
    @Nullable
    private final Runnable runnable;

    public ExtendableSpriteButton(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component text,
            int txtColor,
            ResourceLocation texLocation,
            TextureAtlasSprite spriteWhenOn,
            TextureAtlasSprite spriteWhenOnHover,
            TextureAtlasSprite spriteWhenInactive,
            @Nullable Runnable onPressListener
    ) {
        super(font, x, y, width, height, text, txtColor, texLocation, spriteWhenOn, spriteWhenOnHover, spriteWhenInactive);
        runnable = onPressListener;
    }

    @Override
    public void onPress() {
        if (runnable != null) runnable.run();
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        var sprite = active
                ? (isHovered ? spriteWhenOnHover : spriteWhenOn)
                : spriteWhenInactive;
        renderButtonSprite(poseStack, sprite);
        renderInnerMessage(poseStack, getMessage());
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    public static class Basic extends ExtendableSpriteButton {
        public Basic(Font font, int x, int y, int width, int height, Component text, @Nullable Runnable onPressListener) {
            super(
                    font, x, y, width, height, text, ~0,
                    ParCoolGuiTextureAtlas.TEXTURE_LOCATION,
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_INACTIVE),
                    onPressListener
            );
        }
    }
}
