package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.md.resource.PageEntry;
import com.alrex.parcool.client.md.resource.PageGroupEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ParCoolGuidePageList extends AbstractWidget {
    private static final int ROW_HEIGHT = 13;
    private final List<Tuple<Component, List<Tuple<Component, PageEntry>>>> pages;
    private final int innerHeight;
    private final Font font;
    private final int lineColor;
    private final int textColor;
    private final int groupColor;
    @Nullable
    private final Consumer<PageEntry> pageSelectionListener;
    private float scrollY;
    @Nullable
    private PageEntry hoveredItem;

    public ParCoolGuidePageList(Font font, List<PageGroupEntry> pageGroups, Style groupStyle, Style pageStyle, int x, int y, int width, int height, int lineColor, @Nullable Consumer<PageEntry> pageSelectionListener) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.pageSelectionListener = pageSelectionListener;
        this.pages = new ArrayList<>();
        for (var group : pageGroups) {
            pages.add(new Tuple<>(
                    Component.translatable(group.translationKey()).withStyle(groupStyle),
                    group.content().stream().map(it -> new Tuple<Component, PageEntry>(
                            Component.translatable(it.translationKey()).withStyle(pageStyle), it
                    )).toList()
            ));
        }
        var innerHeight = 0;
        for (var group : pages) {
            innerHeight += (1 + group.getB().size()) * (ROW_HEIGHT + 1);
        }
        this.innerHeight = innerHeight;
        var txtColor = pageStyle.getColor();
        this.textColor = txtColor != null ? txtColor.getValue() : ~0;
        txtColor = groupStyle.getColor();
        this.groupColor = txtColor != null ? txtColor.getValue() : ~0;
        this.lineColor = lineColor;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        setScroll((float) (this.scrollY - scrollDelta * 8));
        return true;
    }

    public void setScroll(float scroll) {
        this.scrollY = scroll;
        if (scrollY > innerHeight - height) scrollY = innerHeight - height;
        if (scrollY < 0) scrollY = 0;
    }

    public float getScroll() {
        return scrollY;
    }

    @Override
    protected boolean isValidClickButton(int click) {
        return hoveredItem != null;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (hoveredItem != null && pageSelectionListener != null) {
            pageSelectionListener.accept(hoveredItem);
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        mouseX -= getX();
        mouseY = (int) (mouseY - getY() + scrollY);
        hoveredItem = null;
        GuiRenderUtil.enableScissorTestInGuiCoordinate(getX(), getY(), width, height);
        var poseStack = graphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(getX(), getY() - scrollY, 0);
            var currentY = 0;
            for (var group : pages) {
                graphics.drawString(font, group.getA(), 5, (int) (currentY + 1 + (ROW_HEIGHT - font.lineHeight) / 2f), groupColor, false);
                graphics.fill(1, currentY - 1 + ROW_HEIGHT / 2, 3, currentY + 1 + ROW_HEIGHT / 2, groupColor);
                currentY += ROW_HEIGHT;
                for (var page : group.getB()) {
                    RenderSystem.setShaderColor(1f, 1f, 1f, 0.2f);
                    graphics.hLine(5, width - 10, currentY, lineColor);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    currentY += 1;

                    if (0 < mouseX && mouseX < width && currentY < mouseY && mouseY < currentY + ROW_HEIGHT) {
                        graphics.fill(0, currentY, width, currentY + ROW_HEIGHT, 0x88DDDDDD);
                        hoveredItem = page.getB();
                    }
                    graphics.drawString(font, page.getA(), 2, (int) (currentY + 1 + (ROW_HEIGHT - font.lineHeight) / 2f), textColor, false);
                    currentY += ROW_HEIGHT;
                }
                graphics.hLine(2, width - 3, currentY, lineColor);
                currentY += 1;
            }
        }
        poseStack.popPose();
        RenderSystem.disableScissor();
    }
}
