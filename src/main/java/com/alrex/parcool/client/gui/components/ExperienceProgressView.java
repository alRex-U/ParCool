package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class ExperienceProgressView extends AbstractWidget {
    private final Font font;
    private float progress;

    public ExperienceProgressView(Font font, int x, int y, Component component) {
        super(x, y, 66, 14, component);
        this.font = font;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        RenderSystem.setShaderTexture(0, ParCoolGuiTextureAtlas.TEXTURE_LOCATION);
        blit(poseStack, x, y, 0, width, height, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX));
        var sprite = ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX_OVERLAY);
        innerBlit(poseStack.last().pose(),
                x, (int) (x + sprite.getWidth() * progress), y, y + sprite.getHeight(), 0,
                sprite.getU0(), sprite.getU(16f * progress), sprite.getV0(), sprite.getV1()
        );
        var message = getMessage();
        var messageWidth = font.width(message);
        font.drawShadow(poseStack, message, x + width - 3 - messageWidth, y + 1 + (height - font.lineHeight) / 2f, ~0);
    }

    public void setProgress(float progress) {
        this.progress = Mth.clamp(progress, 0, 1);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
