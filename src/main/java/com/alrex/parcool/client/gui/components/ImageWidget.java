package com.alrex.parcool.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ImageWidget extends AbstractWidget {
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    private final ResourceLocation textureLocation;

    public ImageWidget(int x, int y, int width, int height, ResourceLocation textureLocation, int texX, int texY, int texWidth, int texHeight) {
        super(x, y, width, height, Component.empty());
        this.textureLocation = textureLocation;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        RenderSystem.setShaderTexture(0, textureLocation);
        blit(poseStack, x, y, width, height, texX, texY, texWidth, texHeight, 256, 256);
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return false;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
