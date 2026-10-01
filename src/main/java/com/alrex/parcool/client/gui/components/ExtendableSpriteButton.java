package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ExtendableSpriteButton extends SpriteButton {

    public ExtendableSpriteButton(
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
        super(font, x, y, width, height, text, txtColor, spriteWhenOn, spriteWhenOnHover, spriteWhenInactive, onPressListener);
    }


    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        var sprite = active
                ? (isHovered ? spriteWhenOnHover : spriteWhenOn)
                : spriteWhenInactive;
        GuiRenderUtil.renderExtendableSprite(poseStack, sprite, x, y, width, height);
        renderInnerMessage(poseStack, getMessage());
    }

    public static class BasicOn extends ExtendableSpriteButton {
        public BasicOn(Font font, int x, int y, int width, int height, Component text, @Nullable Runnable onPressListener) {
            super(
                    font, x, y, width, height, text, ~0,
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_INACTIVE),
                    onPressListener
            );
        }
    }

    public static class BasicOff extends ExtendableSpriteButton {
        public BasicOff(Font font, int x, int y, int width, int height, Component text, @Nullable Runnable onPressListener) {
            super(
                    font, x, y, width, height, text, ~0,
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_OFF),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_OFF_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_INACTIVE),
                    onPressListener
            );
        }
    }
}
