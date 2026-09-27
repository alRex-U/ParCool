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

public class ExtendableSpriteToggleButton extends AbstractExtendableSpriteButton {
    protected final TextureAtlasSprite spriteWhenOffHover;
    protected final TextureAtlasSprite spriteWhenOff;
    private final Component onText;
    private final Component offText;
    private boolean state;
    @Nullable
    private final OnPressedListener pressedListener;

    public ExtendableSpriteToggleButton(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component onText,
            Component offText,
            int txtColor,
            ResourceLocation texLocation,
            TextureAtlasSprite spriteWhenOn,
            TextureAtlasSprite spriteWhenOnHover,
            TextureAtlasSprite spriteWhenOff,
            TextureAtlasSprite spriteWhenOffHover,
            TextureAtlasSprite spriteWhenInactive,
            @Nullable OnPressedListener pressedListener
    ) {
        super(font, x, y, width, height, offText, txtColor, texLocation, spriteWhenOn, spriteWhenOnHover, spriteWhenInactive);
        this.onText = onText;
        this.offText = offText;
        this.spriteWhenOff = spriteWhenOff;
        this.spriteWhenOffHover = spriteWhenOffHover;
        this.pressedListener = pressedListener;
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        var sprite = active
                ? (state ? (isHovered ? spriteWhenOnHover : spriteWhenOn) : (isHovered ? spriteWhenOffHover : spriteWhenOff))
                : spriteWhenInactive;
        renderButtonSprite(poseStack, sprite);
        renderInnerMessage(poseStack, getMessage());
    }

    @Override
    public void onPress() {
        state = !state;
        setMessage(state ? onText : offText);
        if (pressedListener != null) pressedListener.onPress(state);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void on() {
        setState(true);
    }

    public void off() {
        setState(false);
    }

    public interface OnPressedListener {
        void onPress(boolean state);
    }

    public static class Basic extends ExtendableSpriteToggleButton {
        public Basic(Font font, int x, int y, int width, int height, Component onText, Component offText, @Nullable OnPressedListener onPressListener) {
            super(
                    font, x, y, width, height, onText, offText, ~0,
                    ParCoolGuiTextureAtlas.TEXTURE_LOCATION,
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
