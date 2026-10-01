package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSpriteButton extends AbstractButton {
    protected final TextureAtlasSprite spriteWhenInactive;
    protected final TextureAtlasSprite spriteWhenOn;
    protected final TextureAtlasSprite spriteWhenOnHover;
    protected final Font font;
    protected final int txtColor;

    public AbstractSpriteButton(
            Font font,
            int x,
            int y,
            int width,
            int height,
            Component text,
            int txtColor,
            TextureAtlasSprite spriteWhenOn,
            TextureAtlasSprite spriteWhenOnHover,
            TextureAtlasSprite spriteWhenInactive
    ) {
        super(x, y, width, height, text);
        this.font = font;
        this.txtColor = txtColor;
        this.spriteWhenOn = spriteWhenOn;
        this.spriteWhenInactive = spriteWhenInactive;
        this.spriteWhenOnHover = spriteWhenOnHover;
    }

    protected void renderInnerMessage(PoseStack poseStack, Component message) {
        font.draw(poseStack, message, x + (width - font.width(message)) / 2f, 0.5f + y + (height - font.lineHeight) / 2f, txtColor);
    }
}
