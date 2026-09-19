package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.gui.components.ParCoolGuidePageList;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.alrex.parcool.util.ColorUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ParCoolGuideFullScreen extends Screen {
    protected ParCoolGuideFullScreen(ResourceLocation dataLocation) {
        this(dataLocation, new GuidePageStack(null));
    }

    protected ParCoolGuideFullScreen(ResourceLocation dataLocation, GuidePageStack pageStack) {
        super(Component.empty());
        var content = GuideResourceManager.getInstance().getResource().get(dataLocation);
        if (content == null) openSidePanel = true;
        this.pageStack = pageStack;
        this.pageStack.setContentChangedListener(null);
        this.pageStack.pushPage(dataLocation);
        this.pageStack.setContentChangedListener(this::rebuildWidgets);
    }

    private final GuidePageStack pageStack;
    @Nullable
    private ParCoolGuidePageList pageList;
    private boolean openSidePanel;

    @Override
    protected void init() {
        super.init();
        var colors = GuiColorPallet.DEFAULT_LIGHT;
        var content = pageStack.getCurrentContent();
        addRenderableOnly(new CardPanel(0, 0, width, height, ColorUtil.withAlpha(content != null ? colors.surface() : colors.background(), 0xE8)));
        int sideBarWidth = openSidePanel ? ParCoolGuideScreen.SIDE_PANEL_WIDTH_OPENED : ParCoolGuideScreen.SIDE_PANEL_WIDTH_CLOSED;
        if (content != null) {
            addRenderableWidget(
                    new MarkdownWidget(
                            font,
                            sideBarWidth + 4,
                            0,
                            width - sideBarWidth - 7,
                            height,
                            content,
                            colors.onSurface(),
                            (location) -> Minecraft.getInstance().setScreen(new ParCoolGuideFullScreen(location, pageStack)),
                            (url) -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen((b) -> this.confirmLink(b, url), url, false))
                    )
            );
        }
        if (openSidePanel) {
            addRenderableOnly(new CardPanel(0, 0, sideBarWidth, height, ColorUtil.withAlpha(colors.surface(), 0xDD), colors.shadow()));
            addRenderableOnly(new CardPanel(0, 0, sideBarWidth, 13, colors.surface(), colors.shadow()));
            addRenderableWidget(new ParCoolTabletScreen.IconButton.SlideToLeft(sideBarWidth - 12, 1, this::openOrCloseSidePanel));
            var oldPageList = pageList;
            pageList = addRenderableWidget(
                    new ParCoolGuidePageList(
                            font,
                            GuideResourceManager.getInstance().getResource().getPages(),
                            Style.EMPTY.withColor(colors.primary()).withBold(true),
                            Style.EMPTY.withColor(colors.onSurface()),
                            0,
                            13,
                            sideBarWidth,
                            height - 13,
                            colors.separator(),
                            (page) -> pageStack.pushPage(page.resourceLocation())
                    )
            );
            if (oldPageList != null) {
                pageList.setScroll(oldPageList.getScroll());
            }
        } else {
            addRenderableOnly(new CardPanel(0, 0, sideBarWidth, height, colors.surface(), colors.shadow()));
            addRenderableWidget(new ParCoolTabletScreen.IconButton.Hamburger(1, 1, this::openOrCloseSidePanel));
            addRenderableWidget(new ParCoolTabletScreen.IconButton.Shrink(1, height - 13, this::openAsNormalScreen));
        }
    }

    protected void confirmLink(boolean confirmed, String uri) {
        if (confirmed) {
            Util.getPlatform().openUri(uri);
        }
        if (minecraft != null) minecraft.setScreen(this);
    }

    private void openOrCloseSidePanel() {
        openSidePanel = !openSidePanel;
        rebuildWidgets();
    }

    private void openAsNormalScreen() {
        if (minecraft != null) {
            minecraft.setScreen(new ParCoolGuideScreen(pageStack.getCurrentPageID(), pageStack));
        }
    }
}
