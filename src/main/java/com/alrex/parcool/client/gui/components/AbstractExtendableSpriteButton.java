package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractExtendableSpriteButton extends AbstractButton {
    protected final TextureAtlasSprite spriteWhenInactive;
    protected final TextureAtlasSprite spriteWhenOn;
    protected final TextureAtlasSprite spriteWhenOnHover;
    protected final Font font;
    protected final ResourceLocation texLocation;
    protected final int txtColor;

    public AbstractExtendableSpriteButton(
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
            TextureAtlasSprite spriteWhenInactive
    ) {
        super(x, y, width, height, text);
        this.font = font;
        this.txtColor = txtColor;
        this.texLocation = texLocation;
        this.spriteWhenOn = spriteWhenOn;
        this.spriteWhenInactive = spriteWhenInactive;
        this.spriteWhenOnHover = spriteWhenOnHover;
    }

    protected void renderButtonSprite(PoseStack poseStack, TextureAtlasSprite sprite) {
        int halfWidth = width / 2, halfHeight = height / 2;
        int spriteWidth = sprite.getWidth(), spriteHeight = sprite.getHeight();

        RenderSystem.setShaderTexture(0, texLocation);
        var pose = poseStack.last().pose();
        float midU = sprite.getU(16. * halfWidth / spriteWidth), midV = sprite.getV(16. * halfHeight / spriteHeight);
        innerBlit(
                pose,
                x, x + halfWidth,
                y, y + halfHeight,
                0 /*z pos*/,
                sprite.getU0(), midU,
                sprite.getV0(), midV
        );
        innerBlit(
                pose,
                x + halfWidth, x + width,
                y, y + halfHeight,
                0,
                midU, sprite.getU1(),
                sprite.getV0(), midV
        );
        innerBlit(
                pose,
                x, x + halfWidth,
                y + halfHeight, y + height,
                0,
                sprite.getU0(), midU,
                midV, sprite.getV1()
        );
        innerBlit(
                pose,
                x + halfWidth, x + width,
                y + halfHeight, y + height,
                0,
                midU, sprite.getU1(),
                midV, sprite.getV1()
        );
    }

    protected void renderInnerMessage(PoseStack poseStack, Component message) {
        font.draw(poseStack, message, x + (width - font.width(message)) / 2f, 0.5f + y + (height - font.lineHeight) / 2f, txtColor);
    }
}
