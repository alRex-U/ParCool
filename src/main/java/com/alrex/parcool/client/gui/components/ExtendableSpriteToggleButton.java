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
public class ExtendableSpriteToggleButton extends SpriteToggleButton {

    public ExtendableSpriteToggleButton(Font font, int x, int y, int width, int height, Component onText, Component offText, int txtColor, TextureAtlasSprite spriteWhenOn, TextureAtlasSprite spriteWhenOnHover, TextureAtlasSprite spriteWhenOff, TextureAtlasSprite spriteWhenOffHover, TextureAtlasSprite spriteWhenInactive, @Nullable OnPressedListener pressedListener) {
        super(font, x, y, width, height, onText, offText, txtColor, spriteWhenOn, spriteWhenOnHover, spriteWhenOff, spriteWhenOffHover, spriteWhenInactive, pressedListener);
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        var sprite = active
                ? (state ? (isHovered ? spriteWhenOnHover : spriteWhenOn) : (isHovered ? spriteWhenOffHover : spriteWhenOff))
                : spriteWhenInactive;
        GuiRenderUtil.renderExtendableSprite(poseStack, sprite, x, y, width, height);
        renderInnerMessage(poseStack, getMessage());
    }

    public static class Basic extends ExtendableSpriteToggleButton {
        public Basic(Font font, int x, int y, int width, int height, Component onText, Component offText, @Nullable OnPressedListener onPressListener) {
            super(
                    font, x, y, width, height, onText, offText, ~0,
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_ON_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_OFF),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_OFF_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON_INACTIVE),
                    onPressListener
            );
        }
    }
}
