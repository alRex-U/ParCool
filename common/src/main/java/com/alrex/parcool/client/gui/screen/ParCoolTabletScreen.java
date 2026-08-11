package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ParCoolTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/gui/parcool_screen.png");
    protected static final int FRAME_WIDTH = 256;
    protected static final int FRAME_HEIGHT = 160;
    protected static final int CONTENT_WIDTH = 246;
    protected static final int CONTENT_HEIGHT = 136;
    protected final GuiColorPallet colors;
    private String urlBarText;
    private int frameOffsetX = 0;
    private int frameOffsetY = 0;
    protected int contentOffsetX = 0;
    protected int contentOffsetY = 0;
    private IconButton backButton;

    protected ParCoolTabletScreen(Component title, GuiColorPallet colors) {
        this(title, colors, "prcl://index");
    }

    protected ParCoolTabletScreen(Component title, GuiColorPallet colors, String initialTopBarText) {
        super(title);
        this.colors = colors;
        this.urlBarText = initialTopBarText;
    }

    @Override
    protected void init() {
        super.init();
        frameOffsetX = (width - FRAME_WIDTH) / 2;
        frameOffsetY = (height - FRAME_HEIGHT) / 2;
        contentOffsetX = frameOffsetX + 5;
        contentOffsetY = frameOffsetY + 19;
        backButton = addWidget(new IconButton.Back(frameOffsetX + 7, frameOffsetY + 6, this::onPressTobBarButton));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        renderBackground(poseStack);
        poseStack.pushPose();
        {
            renderContent(poseStack, mouseX, mouseY, partial);
            renderFrame(poseStack, partial);
            backButton.render(poseStack, mouseX, mouseY, partial);
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

    protected static class IconButton extends AbstractButton {
        private final TextureAtlasSprite sprite;
        @Nullable
        private final Runnable pressListener;

        public IconButton(int x, int y, TextureAtlasSprite sprite, @Nullable Runnable listener) {
            super(x, y, 11, 11, Component.empty());
            this.sprite = sprite;
            this.pressListener = listener;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
            if (isHovered) {
                RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1f);
            }
            RenderSystem.setShaderTexture(0, ParCoolGuiTextureAtlas.TEXTURE_LOCATION);
            blit(poseStack, x, y, 0, width, height, sprite);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        @Override
        public void onPress() {
            if (pressListener != null) pressListener.run();
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
        }

        protected static class Back extends IconButton {
            public Back(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_BACK), listener);
            }
        }

        protected static class Home extends IconButton {
            public Home(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HOME), listener);
            }
        }

        protected static class Hamburger extends IconButton {
            public Hamburger(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HAMBURGER), listener);
            }
        }

        protected static class SlideToLeft extends IconButton {
            public SlideToLeft(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_CLOSE), listener);
            }
        }
    }

    protected void confirmLink(boolean confirmed, String uri) {
        if (confirmed) {
            Util.getPlatform().openUri(uri);
        }
        if (minecraft != null) minecraft.setScreen(this);
    }

    protected void onPressTobBarButton() {
    }
}
