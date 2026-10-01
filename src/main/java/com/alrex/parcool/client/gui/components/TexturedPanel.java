package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class TexturedPanel extends AbstractWidget {
    public static class Textures {
        public static final ResourceLocation SETTING_TOP = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/setting/top.png");
        public static final ResourceLocation SETTING_SURFACE = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/setting/surface.png");
        public static final ResourceLocation SETTING_CARD = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/setting/card.png");
        public static final ResourceLocation SETTING_BACKGROUND = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/setting/background.png");
        public static final ResourceLocation SKILLTREE_TOP = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/skilltree/top.png");
        public static final ResourceLocation SKILLTREE_SURFACE = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/skilltree/surface.png");
        public static final ResourceLocation SKILLTREE_BACKGROUND = new ResourceLocation(ParCool.MOD_ID, "textures/gui/panel/skilltree/background.png");
    }

    private final ResourceLocation texLocation;
    private final int texWidth;
    private final int texHeight;
    private int borderColor;

    public TexturedPanel(int x, int y, int width, int height, ResourceLocation texLocation) {
        this(x, y, width, height, texLocation, 16, 16);
    }

    public TexturedPanel(int x, int y, int width, int height, ResourceLocation texLocation, int texWidth, int texHeight) {
        super(x, y, width, height, Component.empty());
        this.texLocation = texLocation;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public TexturedPanel withBorder(int color) {
        this.borderColor = color;
        return this;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (ColorUtil.alphaOf(borderColor) != 0) {
            fill(poseStack, x, y - 1, x + width, y, borderColor);
            fill(poseStack, x, y + height, x + width, y + height + 1, borderColor);
            fill(poseStack, x - 1, y, x, y + height, borderColor);
            fill(poseStack, x + width, y, x + width + 1, y + height, borderColor);
        }
        RenderSystem.setShaderTexture(0, texLocation);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        blit(poseStack, x, y, 0, 0, 0, width, height, texWidth, texHeight);
    }

    @Override
    protected boolean isValidClickButton(int click) {
        return false;
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
