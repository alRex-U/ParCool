package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class ParCoolTabletScreen extends Screen {
    protected static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ParCool.MOD_ID, "textures/gui/parcool_screen.png");
    protected static final int FRAME_WIDTH = 256;
    protected static final int FRAME_HEIGHT = 160;
    protected static final int CONTENT_WIDTH = 246;
    protected static final int CONTENT_HEIGHT = 136;
    private String urlBarText;
    private int frameOffsetX = 0;
    private int frameOffsetY = 0;
    protected int contentOffsetX = 0;
    protected int contentOffsetY = 0;

    protected ParCoolTabletScreen(Component title) {
        this(title, "prcl://index");
    }

    protected ParCoolTabletScreen(Component title, String initialTopBarText) {
        super(title);
        this.urlBarText = initialTopBarText;
    }

    @Override
    protected void init() {
        super.init();
        frameOffsetX = (width - FRAME_WIDTH) / 2;
        frameOffsetY = (height - FRAME_HEIGHT) / 2;
        contentOffsetX = frameOffsetX + 5;
        contentOffsetY = frameOffsetY + 19;
        addRenderableWidget(new BackButton(frameOffsetX + 7, frameOffsetY + 6));
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        renderBackground(poseStack);
        poseStack.pushPose();
        {
            renderContent(poseStack, mouseX, mouseY, partial);
            renderFrame(poseStack, partial);
        }
        poseStack.popPose();
    }

    private void renderFrame(PoseStack poseStack, float partial) {
        poseStack.pushPose();
        {
            RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
            blit(poseStack, frameOffsetX, frameOffsetY, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
            font.draw(poseStack, urlBarText, frameOffsetX + 30, frameOffsetY + 8, 0x37474F);
        }
        poseStack.popPose();
    }

    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        super.render(poseStack, mouseX, mouseY, partial);
    }

    protected void setTopBarText(String urlLikeText) {
        urlLikeText = font.plainSubstrByWidth(urlLikeText, 217);
        this.urlBarText = urlLikeText;
    }

    private static class BackButton extends AbstractButton {
        public BackButton(int x, int y) {
            super(x, y, 10, 10, Component.empty());
        }

        @Override
        public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
            if (isHovered) {
                RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 0.8f);
            }
            blit(poseStack, x, y, 240, 160, width, height);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        @Override
        public void onPress() {
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }
    }

    protected void confirmLink(boolean confirmed, String uri) {
        if (confirmed) {
            Util.getPlatform().openUri(uri);
        }
        if (minecraft != null) minecraft.setScreen(this);
    }
}
