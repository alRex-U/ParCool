package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.gui.components.ParCoolGuidePageList;
import com.alrex.parcool.client.md.CompiledMarkdown;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.md.resource.PageEntry;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Stack;

@Environment(EnvType.CLIENT)
public class ParCoolGuideScreen extends ParCoolTabletScreen {
    private static final int SIDE_PANEL_WIDTH_OPENED = 90;
    private static final int SIDE_PANEL_WIDTH_CLOSED = 13;

    private record PageStackEntry(ResourceLocation page, @Nullable CompiledMarkdown content) {
    }

    private final Stack<PageStackEntry> pageStack = new Stack<>();
    @Nullable
    private ParCoolGuidePageList pageList;
    private boolean openSidePanel;

    public ParCoolGuideScreen(ResourceLocation dataLocation) {
        super(Component.empty(), GuiColorPallet.DEFAULT_LIGHT);
        var content = GuideResourceManager.getInstance().getResource().get(dataLocation);
        if (content == null) openSidePanel = true;
        pageStack.push(new PageStackEntry(dataLocation, content));
    }

    @Override
    protected void init() {
        super.init();
        updateTopBarText();
        var content = getCurrentContent();
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
                            this::pushPage
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

    private void pushPage(PageEntry page) {
        if (!pageStack.isEmpty()) {
            var currentPage = pageStack.lastElement();
            if (currentPage != null && currentPage.page.equals(page.resourceLocation())) return;
        }
        var pageLocation = page.resourceLocation();
        pageStack.push(new PageStackEntry(pageLocation, GuideResourceManager.getInstance().getResource().get(page)));
        rebuildWidgets();
        updateTopBarText();
    }

    private void popPage() {
        if (!pageStack.isEmpty()) pageStack.pop();
        if (pageStack.isEmpty()) {
            Minecraft.getInstance().setScreen(null);
        } else {
            rebuildWidgets();
            updateTopBarText();
        }
    }

    public ResourceLocation getCurrentPageId() {
        return pageStack.lastElement().page();
    }

    @Nullable
    public CompiledMarkdown getCurrentContent() {
        if (pageStack.isEmpty()) return null;
        return pageStack.lastElement().content();
    }

    private void updateTopBarText() {
        if (getCurrentContent() == null || pageStack.isEmpty()) {
            setTopBarText("prcl://guide/not_found");
        } else {
            var currentPage = getCurrentPageId();
            setTopBarText(String.format("prcl://guide/%s/%s", currentPage.getNamespace(), currentPage.getPath()));
        }
    }

    @Override
    protected void onPressTobBarButton() {
        popPage();
    }

    private void openOrCloseSidePanel() {
        openSidePanel = !openSidePanel;
        rebuildWidgets();
    }
}
