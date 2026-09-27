package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.CardPanel;
import com.alrex.parcool.client.gui.components.ParCoolGuidePageList;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.md.ui.MarkdownWidget;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ParCoolGuideScreen extends ParCoolTabletScreen {
    public static final int SIDE_PANEL_WIDTH_OPENED = 90;

    private final GuidePageStack pageStack;

    @Nullable
    private ParCoolGuidePageList pageList;
    private boolean openSidePanel;

    public ParCoolGuideScreen(ResourceLocation dataLocation) {
        this(dataLocation, new GuidePageStack(null));
    }

    public ParCoolGuideScreen(ResourceLocation dataLocation, GuidePageStack pageStack) {
        super(Component.empty(), GuiColorPallet.DEFAULT_LIGHT, true);
        var content = GuideResourceManager.getInstance().getResource().get(dataLocation);
        if (content == null) openSidePanel = true;
        this.pageStack = pageStack;
        this.pageStack.setContentChangedListener(null);
        this.pageStack.pushPage(dataLocation);
        this.pageStack.setContentChangedListener(this::rebuildWidgets);
    }

    @Override
    protected void initWidgets() {
        var content = pageStack.getCurrentContent();
        addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, contentWidth, contentHeight, ColorUtil.withAlpha(content != null ? colors.surface() : colors.background(), isFullscreen() ? 0xE8 : 0xFF)));
        int sideBarWidth = openSidePanel ? SIDE_PANEL_WIDTH_OPENED : 0;
        if (content != null) {
            addRenderableWidget(
                    new MarkdownWidget(
                            font,
                            contentOffsetX + sideBarWidth + 4,
                            contentOffsetY,
                            contentWidth - sideBarWidth - 7,
                            contentHeight,
                            content,
                            colors.onSurface(),
                            pageStack::pushPage,
                            (url) -> Minecraft.getInstance().setScreen(new ConfirmLinkScreen((b) -> this.confirmLink(b, url), url, false))
                    )
            );
        }
        if (openSidePanel) {
            addRenderableOnly(new CardPanel(contentOffsetX, contentOffsetY, sideBarWidth, contentHeight, ColorUtil.withAlpha(colors.surface(), isFullscreen() ? 0xDD : 0xFF), colors.shadow()));
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
                            contentHeight - 13,
                            colors.separator(),
                            (page) -> pageStack.pushPage(page.resourceLocation())
                    )
            );
            if (oldPageList != null) {
                pageList.setScroll(oldPageList.getScroll());
            }
        }
        updateTopBarText();
    }

    @Nullable
    @Override
    protected SideBarButtons getSideBarButtons() {
        if (openSidePanel) return null;
        return new SideBarButtons(
                List.of(
                        new Tuple<>(IconButton.Hamburger::new, this::openOrCloseSidePanel)
                ),
                List.of(
                        isFullscreen()
                                ? new Tuple<>(IconButton.Shrink::new, this::switchFullScreen)
                                : new Tuple<>(IconButton.Expand::new, this::switchFullScreen)
                )
        );
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        super.renderContent(poseStack, mouseX, mouseY, partial);
    }

    private void updateTopBarText() {
        if (pageStack.getCurrentContent() == null || pageStack.isEmpty()) {
            setTopBarText(Component.translatable("parcool.gui.top.guide.not_found"));
        } else {
            var currentPage = pageStack.getCurrentPageID();
            if (currentPage != null)
                setTopBarText(Component.translatable("parcool.gui.top.guide.page", currentPage.getNamespace(), currentPage.getPath()));
        }
    }

    @Override
    protected void onPressTobBarButton() {
        pageStack.popPage();
    }

    private void openOrCloseSidePanel() {
        openSidePanel = !openSidePanel;
        rebuildWidgets();
    }

    private void switchFullScreen() {
        setFullscreen(!isFullscreen());
    }
}
