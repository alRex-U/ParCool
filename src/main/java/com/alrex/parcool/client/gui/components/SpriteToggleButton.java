package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpriteToggleButton extends AbstractSpriteButton {
    protected final TextureAtlasSprite spriteWhenOffHover;
    protected final TextureAtlasSprite spriteWhenOff;
    private final Component onText;
    private final Component offText;
    protected boolean state;
    @Nullable
    private OnPressedListener pressedListener;

    public SpriteToggleButton(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component onText,
            Component offText,
            int txtColor,
            TextureAtlasSprite spriteWhenOn,
            TextureAtlasSprite spriteWhenOnHover,
            TextureAtlasSprite spriteWhenOff,
            TextureAtlasSprite spriteWhenOffHover,
            TextureAtlasSprite spriteWhenInactive,
            @Nullable OnPressedListener pressedListener
    ) {
        super(font, x, y, width, height, offText, txtColor, spriteWhenOn, spriteWhenOnHover, spriteWhenInactive);
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
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        blit(poseStack, x, y, 0, width, height, sprite);
        renderInnerMessage(poseStack, getMessage());
    }

    @Override
    public void onPress() {
        setState(!state);
        if (pressedListener != null) pressedListener.onPress(state);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    public void setState(boolean state) {
        this.state = state;
        setMessage(state ? onText : offText);
    }

    public void on() {
        setState(true);
    }

    public void off() {
        setState(false);
    }

    public void setPressedListener(@Nullable OnPressedListener pressedListener) {
        this.pressedListener = pressedListener;
    }

    public interface OnPressedListener {
        void onPress(boolean state);
    }

    public static class Basic extends SpriteToggleButton {
        public Basic(Font font, int x, int y, @Nullable OnPressedListener onPressListener) {
            super(
                    font, x, y, 28, 13, Component.empty(), Component.empty(), ~0,
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_SWITCH_ON),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_SWITCH_ON_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_SWITCH_OFF),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_SWITCH_OFF_HOVER),
                    ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_SWITCH_INACTIVE),
                    onPressListener
            );
        }
    }
}
