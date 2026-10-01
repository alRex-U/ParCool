package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ParCoolTabletScreen extends Screen {
    public static final ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/gui/parcool_screen.png");
    private static final int FRAME_WIDTH = 256;
    private static final int FRAME_HEIGHT = 160;
    private static final int CONTENT_WIDTH = 246;
    private static final int CONTENT_HEIGHT = 136;
    protected final GuiColorPallet colors;
    private Component topBarText = Component.empty();
    private AbstractButton topBarButton;
    private int frameOffsetX = 0;
    private int frameOffsetY = 0;
    protected int contentOffsetX = 0;
    protected int contentOffsetY = 0;
    protected int contentWidth = 0;
    protected int contentHeight = 0;
    private boolean fullscreen;
    protected final boolean openedByGuideItem;

    protected ParCoolTabletScreen(Component title, GuiColorPallet colors, boolean openedByGuideItem) {
        super(title);
        this.colors = colors;
        this.openedByGuideItem = openedByGuideItem;
        this.fullscreen = !openedByGuideItem;
    }


    @Override
    protected final void init() {
        super.init();
        frameOffsetX = (width - FRAME_WIDTH) / 2;
        frameOffsetY = (height - FRAME_HEIGHT) / 2;
        if (fullscreen) {
            contentOffsetX = 0;
            contentOffsetY = 0;
            contentWidth = width;
            contentHeight = height;
        } else {
            contentOffsetX = frameOffsetX + 5;
            contentOffsetY = frameOffsetY + 19;
            contentWidth = CONTENT_WIDTH;
            contentHeight = CONTENT_HEIGHT;
        }
        var sideBarButtons = getSideBarButtons();
        if (sideBarButtons == null) {
            initWidgets();
        } else {
            contentOffsetX += 13;
            contentWidth -= 13;
            initWidgets();
            addRenderableOnly(new CardPanel(contentOffsetX - 13, contentOffsetY, 13, contentHeight, colors.surface(), colors.shadow()));
            var buttonOffsetX = contentOffsetX - 12;
            var buttonOffsetY = contentOffsetY + 1;
            for (var i = 0; i < sideBarButtons.topButtonProviders.size(); i++) {
                var topButtonProvider = sideBarButtons.topButtonProviders.get(i);
                var button = addRenderableWidget(topButtonProvider.getA().create(buttonOffsetX, buttonOffsetY, topButtonProvider.getB()));
                buttonOffsetY += button.getHeight() + 1;
            }
            buttonOffsetY = contentOffsetY + contentHeight - 13;
            for (var i = 0; i < sideBarButtons.bottomButtonProviders.size(); i++) {
                var bottomButtonProvider = sideBarButtons.bottomButtonProviders.get(i);
                var button = addRenderableWidget(bottomButtonProvider.getA().create(buttonOffsetX, buttonOffsetY, bottomButtonProvider.getB()));
                buttonOffsetY -= button.getHeight() + 1;
            }
        }
        topBarButton = addWidget(new IconButton.Back(frameOffsetX + 7, frameOffsetY + 6, this::onPressTobBarButton));
        topBarButton.active = !fullscreen;
    }

    protected void initWidgets() {
    }

    public void setFullscreen(boolean fullscreen) {
        if (!openedByGuideItem) fullscreen = true;
        if (this.fullscreen != fullscreen) {
            this.fullscreen = fullscreen;
            rebuildWidgets();
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    @Nullable
    protected SideBarButtons getSideBarButtons() {
        return null;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (minecraft == null) return;
        renderBackground(poseStack);
        poseStack.pushPose();
        {
            renderContent(poseStack, mouseX, mouseY, partial);
            if (!fullscreen) {
                renderFrame(poseStack, partial);
                topBarButton.render(poseStack, mouseX, mouseY, partial);
            }
        }
        poseStack.popPose();
    }

    private void renderFrame(PoseStack poseStack, float partial) {
        poseStack.pushPose();
        {
            RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
            blit(poseStack, frameOffsetX, frameOffsetY, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
            GuiRenderUtil.enableScissorTestInGuiCoordinate(frameOffsetX + 22, frameOffsetY + 7, 224, font.lineHeight + 5);
            font.draw(poseStack, topBarText, frameOffsetX + 23, frameOffsetY + 8, 0x37474F);
            RenderSystem.disableScissor();
        }
        poseStack.popPose();
    }

    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        super.render(poseStack, mouseX, mouseY, partial);
    }

    protected void setTopBarText(Component text) {
        this.topBarText = text;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var iterator = this.children().listIterator(this.children().size()); iterator.hasPrevious(); ) {
            var listener = iterator.previous();
            if (listener.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(listener);
                if (button == 0) this.setDragging(true);
                return true;
            }
        }
        return false;
    }

    public static class IconButton extends AbstractButton {
        private final TextureAtlasSprite sprite;
        @Nullable
        private final Runnable pressListener;

        public IconButton(int x, int y, TextureAtlasSprite sprite, @Nullable Runnable listener) {
            super(x, y, 11, 11, Component.empty());
            this.sprite = sprite;
            this.pressListener = listener;
        }

        @Override
        public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
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
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        public interface Provider {
            IconButton create(int x, int y, @Nullable Runnable listener);
        }

        public static class Back extends IconButton {
            public Back(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_BACK), listener);
            }
        }

        public static class Home extends IconButton {
            public Home(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HOME), listener);
            }
        }

        public static class Hamburger extends IconButton {
            public Hamburger(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_HAMBURGER), listener);
            }
        }

        public static class SlideToLeft extends IconButton {
            public SlideToLeft(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_CLOSE), listener);
            }
        }

        public static class Expand extends IconButton {
            public Expand(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_EXPAND), listener);
            }
        }

        public static class Shrink extends IconButton {
            public Shrink(int x, int y, @Nullable Runnable listener) {
                super(x, y, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BUTTON_SHRINK), listener);
            }
        }
    }

    protected record SideBarButtons(List<Tuple<IconButton.Provider, Runnable>> topButtonProviders,
                                    List<Tuple<IconButton.Provider, Runnable>> bottomButtonProviders) {
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
