package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.gui.components.ParCoolGuidePageList;
import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.md.resource.PageEntry;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ParCoolGuideScreen extends ParCoolTabletScreen {
    private static final int SIDE_PANEL_WIDTH_OPENED = 90;
    private static final int SIDE_PANEL_WIDTH_CLOSED = 13;
    @Nullable
    private CompiledMarkdown content;
    private ResourceLocation currentPage;
    @Nullable
    private ParCoolGuidePageList pageList;
    private boolean openSidePanel;

    public ParCoolGuideScreen(ResourceLocation dataLocation) {
        super(Component.empty(), GuiColorPallet.DEFAULT_LIGHT);
        currentPage = dataLocation;
        content = GuideResourceManager.getInstance().getResource().get(dataLocation);
        if (content == null) openSidePanel = true;
    }

    @Override
    protected void init() {
        super.init();
        updateTopBarText();
        addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, CONTENT_WIDTH, CONTENT_HEIGHT, content != null ? colors.surface() : colors.background()));
        int sideBarWidth = openSidePanel ? SIDE_PANEL_WIDTH_OPENED : SIDE_PANEL_WIDTH_CLOSED;
        if (content != null) {
            addRenderableWidget(
                    new MarkdownWidget(
                            font,
                            contentOffsetX + sideBarWidth + 4,
                            contentOffsetY,
                            CONTENT_WIDTH - sideBarWidth - 7,
                            CONTENT_HEIGHT,
                            content,
                            colors.onSurface(),
                            (location) -> Minecraft.getInstance().setScreen(new ParCoolGuideScreen(location)),
                            (url) -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen((b) -> this.confirmLink(b, url), url, false))
                    )
            );
        }
        if (openSidePanel) {
            addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, sideBarWidth, CONTENT_HEIGHT, colors.surface(), colors.shadow()));
            addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, sideBarWidth, 13, colors.surface(), colors.shadow()));
            addRenderableWidget(new IconButton.SlideToLeft(contentOffsetX + sideBarWidth - 12, contentOffsetY + 1, this::openOrCloseSidePanel));
            var oldPageList = pageList;
            pageList = addRenderableWidget(
                    new ParCoolGuidePageList(
                            font,
                            GuideResourceManager.getInstance().getResource().getPages(),
                            Style.EMPTY.withColor(colors.primary()).withBold(true),
                            Style.EMPTY.withColor(colors.onSurface()),
                            contentOffsetX,
                            contentOffsetY + 13,
                            sideBarWidth,
                            CONTENT_HEIGHT - 13,
                            colors.separator(),
                            this::changePage
                    )
            );
            if (oldPageList != null) {
                pageList.setScroll(oldPageList.getScroll());
            }
        } else {
            addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, sideBarWidth, CONTENT_HEIGHT, colors.surface(), colors.shadow()));
            addRenderableWidget(new IconButton.Hamburger(contentOffsetX + 1, contentOffsetY + 1, this::openOrCloseSidePanel));
        }
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        super.renderContent(poseStack, mouseX, mouseY, partial);
    }

    private void changePage(PageEntry page) {
        if (currentPage != null && currentPage.equals(page.resourceLocation())) return;
        currentPage = page.resourceLocation();
        content = GuideResourceManager.getInstance().getResource().get(page);
        rebuildWidgets();
        updateTopBarText();
    }

    private void updateTopBarText() {
        if (content == null) {
            setTopBarText("prcl://guide/not_found");
        } else {
            setTopBarText(String.format("prcl://guide/%s/%s", currentPage.getNamespace(), currentPage.getPath()));
        }
    }

    private void openOrCloseSidePanel() {
        openSidePanel = !openSidePanel;
        rebuildWidgets();
    }
}
