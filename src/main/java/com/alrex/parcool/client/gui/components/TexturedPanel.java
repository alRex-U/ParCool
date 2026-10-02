package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class TexturedPanel extends AbstractWidget {
    public static class Textures {
        public static final ResourceLocation SETTING_TOP = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/top.png");
        public static final ResourceLocation SETTING_SURFACE = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/surface.png");
        public static final ResourceLocation SETTING_CARD = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/card.png");
        public static final ResourceLocation SETTING_BACKGROUND = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/setting/background.png");
        public static final ResourceLocation SKILLTREE_TOP = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/top.png");
        public static final ResourceLocation SKILLTREE_SURFACE = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/surface.png");
        public static final ResourceLocation SKILLTREE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "textures/gui/panel/skilltree/background.png");
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
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (ColorUtil.alphaOf(borderColor) != 0) {
            graphics.fill(getX(), getY() - 1, getX() + width, getY(), borderColor);
            graphics.fill(getX(), getY() + height, getX() + width, getY() + height + 1, borderColor);
            graphics.fill(getX() - 1, getY(), getX(), getY() + height, borderColor);
            graphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, borderColor);
        }
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        graphics.blit(texLocation, getX(), getY(), 0, 0, 0, width, height, texWidth, texHeight);
    }

    @Override
    protected boolean isValidClickButton(int click) {
        return false;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }
}
