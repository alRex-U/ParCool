package com.alrex.parcool.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;

public class WidgetGroup extends AbstractWidget {
    private final List<AbstractWidget> widgets;

    public WidgetGroup(int x, int y, int width, int height, List<AbstractWidget> widgets) {
        super(x, y, width, height, Component.empty());
        this.widgets = widgets;
    }

    @Override
    public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        if (!visible) return;
        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(getX(), getY(), 0);
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            widget.render(graphics, mouseX, mouseY, partial);
        }
        poseStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.mouseClicked(mouseX, mouseY, click)) return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!visible) return;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int mouseX, int mouseY, int key) {
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.keyPressed(mouseX, mouseY, key)) return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int mouseX, int mouseY, int key) {
        if (!visible) return false;
        mouseX -= getX();
        mouseY -= getY();
        for (var widget : widgets) {
            if (widget.keyReleased(mouseX, mouseY, key)) return true;
        }
        return false;
    }
}
