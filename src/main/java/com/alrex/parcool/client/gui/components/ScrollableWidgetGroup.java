package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScrollableWidgetGroup extends WidgetGroup {
    public enum ScrollType {
        HORIZONTAL, VERTICAL
    }

    private final int contentHeight;
    private final int contentWidth;
    private int scrollX;
    private int scrollY;
    private int scrollLimitX;
    private int scrollLimitY;
    private final ScrollType scrollType;

    public ScrollableWidgetGroup(int x, int y, int width, int height, ScrollType scrollType, List<AbstractWidget> widgets) {
        super(x, y, width, height, widgets);
        this.scrollType = scrollType;
        this.contentWidth = widgets.stream().map(it -> it.x + it.getWidth()).max(Integer::compareTo).orElse(1);
        this.contentHeight = widgets.stream().map(it -> it.y + it.getHeight()).max(Integer::compareTo).orElse(1);
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.scrollLimitX = Math.max(contentWidth - width, 0);
    }

    @Override
    public void setHeight(int value) {
        super.setHeight(value);
        this.scrollLimitY = Math.max(contentHeight - height, 0);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        GuiRenderUtil.enableScissorTestInGuiCoordinate(x, y, width, height);
        poseStack.pushPose();
        {
            poseStack.translate(-scrollX, -scrollY, 0);
            super.render(poseStack, mouseX + scrollX, mouseY + scrollY, partial);
        }
        poseStack.popPose();
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        return super.mouseClicked(mouseX + scrollX, mouseY + scrollY, click);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX + scrollX, mouseY + scrollY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        switch (scrollType) {
            case HORIZONTAL -> scrollX += (int) (scroll * -16.);
            case VERTICAL -> scrollY += (int) (scroll * -16.);
        }
        applyScrollLimits();
        return super.mouseScrolled(mouseX + scrollX, mouseY + scrollY, scroll);
    }

    @Override
    public boolean keyPressed(int mouseX, int mouseY, int key) {
        return super.keyPressed(mouseX + scrollX, mouseY + scrollY, key);
    }

    @Override
    public boolean keyReleased(int mouseX, int mouseY, int key) {
        return super.keyReleased(mouseX + scrollX, mouseY + scrollY, key);
    }

    private void applyScrollLimits() {
        if (scrollX > scrollLimitX) scrollX = scrollLimitX;
        if (scrollX < 0) scrollX = 0;
        if (scrollY > scrollLimitY) scrollY = scrollLimitY;
        if (scrollY < 0) scrollY = 0;
    }
}
