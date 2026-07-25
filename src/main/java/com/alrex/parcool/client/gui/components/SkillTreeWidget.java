package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.textures.ParCoolActionsTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextureAtlases;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SkillTreeWidget extends AbstractWidget {
    private final List<SkillTreeActionIcon> icons;
    private final List<ConnectivityWidget> connectivities;
    private final ActionCapabilities capabilities;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    @Nullable
    private Consumer<SkillTree.Entry<?>> selectionListener;
    private double scrollX;
    private double scrollY;
    private float scale = 1f;
    private final int contentWidth;
    private final int contentHeight;

    public SkillTreeWidget(List<SkillTree> skillTrees, ActionCapabilities capabilities, int x, int y, int width, int height, Consumer<SkillTree.Entry<?>> selectionListener) {
        super(x, y, width, height, Component.empty());
        this.capabilities = capabilities;
        this.selectionListener = selectionListener;
        icons = new ArrayList<>();
        connectivities = new ArrayList<>();
        var contentWidth = 0;
        var contentHeight = 0;
        for (var skillTree : skillTrees) {
            var result = init$calculateWidget(skillTree.getRoot(), 32, contentWidth, 0, icons, connectivities, capabilities);
            contentWidth += result.width + 40;
            if (contentHeight < result.height) contentHeight = result.height;
        }
        contentWidth -= 40;
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
    }

    private record CalculateWidgetResult(SkillTreeActionIcon rootIcon, int width, int height) {
    }

    private CalculateWidgetResult init$calculateWidget(SkillTree.Entry<?> entry, int xMargin, int xOffset, int yOffset, List<SkillTreeActionIcon> iconsList, List<ConnectivityWidget> connectivities, ActionCapabilities capabilities) {
        var widget = new SkillTreeActionIcon(entry, xOffset, yOffset);
        var children = entry.getChildren();
        if (children.isEmpty()) {
            iconsList.add(widget);
            return new CalculateWidgetResult(widget, widget.getWidth(), widget.getHeight());
        }
        var width = 0;
        var yMargin = widget.getHeight() * 2;
        var childMaxHeight = 0;
        var childList = new ArrayList<SkillTreeActionIcon>(children.size());
        for (var child : children) {
            var childResult = init$calculateWidget(
                    child,
                    xMargin,
                    xOffset + width,
                    yOffset + yMargin + widget.getHeight(),
                    iconsList,
                    connectivities,
                    capabilities
            );
            childList.add(childResult.rootIcon);
            width += childResult.width + xMargin;
            if (childMaxHeight < childResult.height) {
                childMaxHeight = childResult.height;
            }
        }
        width -= xMargin;
        widget.x = xOffset + (width - widget.getWidth()) / 2; // centering the widget to children
        iconsList.add(widget);
        connectivities.add(new ConnectivityWidget(widget, childList));
        return new CalculateWidgetResult(widget, width, childMaxHeight + yMargin + widget.getHeight());
    }

    @Nullable
    public SkillTree.Entry<?> getSelectedSkill() {
        return selectedSkill;
    }

    void setSelectedSkill(@Nullable SkillTree.Entry<?> selectedSkill) {
        this.selectedSkill = selectedSkill;
        if (selectionListener != null) selectionListener.accept(selectedSkill);
    }

    private double getMouseXInContent(double mouseX) {
        return scrollX + (mouseX - x) / scale;
    }

    private double getMouseYInContent(double mouseY) {
        return scrollY + (mouseY - y) / scale;
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        boolean mouseIsOutOfWidget = mouseX < x || x + width < mouseX || mouseY < y || y + height < mouseY;
        poseStack.pushPose();
        {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.colorMask(false, false, false, false);
            fill(poseStack, x, y, x + width, y + height, ~0);
            RenderSystem.depthFunc(GL11.GL_GEQUAL);
            RenderSystem.colorMask(true, true, true, true);
        }
        poseStack.popPose();

        poseStack.pushPose();
        {
            poseStack.translate(x, y, 0);
            poseStack.scale(scale, scale, 0);
            poseStack.translate(-scrollX, -scrollY, 1);
            int mouseXScaled = mouseIsOutOfWidget ? -1 : (int) getMouseXInContent(mouseX);
            int mouseYScaled = mouseIsOutOfWidget ? -1 : (int) getMouseYInContent(mouseY);
            for (var widget : connectivities) {
                widget.render(poseStack, mouseXScaled, mouseYScaled, partialTick);
            }
            for (var widget : icons) {
                widget.render(poseStack, mouseXScaled, mouseYScaled, partialTick);
            }
        }
        poseStack.popPose();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        scrollX -= dragX / scale;
        scrollY -= dragY / scale;
        if (scrollX < -width) scrollX = -width;
        else if (scrollX > contentWidth) scrollX = contentWidth;
        if (scrollY < -height) scrollY = -height;
        else if (scrollY > contentHeight) scrollY = contentHeight;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        this.scale += (float) scrollDelta / 4f;
        if (this.scale < 0.5f) this.scale = 0.5f;
        else if (this.scale > 2.0f) this.scale = 2.0f;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (var icon : icons) {
                if (icon.isHoveredOrFocused()) {
                    return icon.mouseClicked(getMouseXInContent(mouseX), getMouseYInContent(mouseY), button);
                }
            }
        }
        return false;
    }

    private class ConnectivityWidget extends GuiComponent implements Widget {
        private final SkillTreeActionIcon root;
        private final List<SkillTreeActionIcon> leaves;
        private final int yOfHLine;

        public ConnectivityWidget(SkillTreeActionIcon root, List<SkillTreeActionIcon> leaves) {
            this.root = root;
            this.leaves = Collections.unmodifiableList(leaves);
            var rootBottom = root.y + root.getHeight();
            var leafTop = 1024;
            for (var leaf : leaves) {
                if (leafTop > leaf.y) leafTop = leaf.y;
            }
            yOfHLine = (rootBottom + leafTop) / 2;
        }

        @Override
        public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            var rootYCenter = root.y + root.getHeight() / 2;
            int color = root.getSkillTreeEntry().isUnlocked(capabilities) ? 0xFFAAAAAA : 0xAA777777;
            if (leaves.isEmpty()) return;
            if (leaves.size() == 1) {
                vLine(poseStack, root.x + root.getWidth() / 2, rootYCenter, leaves.get(0).y, color);
                return;
            }
            vLine(poseStack, root.x + root.getWidth() / 2, rootYCenter, yOfHLine, color);
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            for (var leaf : leaves) {
                var x = leaf.x + leaf.getWidth() / 2;
                if (x < minX) minX = x;
                if (maxX < x) maxX = x;
                vLine(poseStack, x, yOfHLine, leaf.y + leaf.getHeight() / 2, color);
            }
            hLine(poseStack, minX, maxX, yOfHLine, color);
        }
    }

    public class SkillTreeActionIcon extends AbstractButton {
        private final SkillTree.Entry<?> entry;

        public SkillTreeActionIcon(SkillTree.Entry<?> entry, int x, int y) {
            super(x, y, 24, 24, Component.empty());
            this.entry = entry;
        }

        public SkillTree.Entry<?> getSkillTreeEntry() {
            return entry;
        }

        @Override
        public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShaderTexture(0, ParCoolActionsTextureAtlas.TEXTURE_LOCATION);
            if (entry.isVisible(capabilities)) {
                if (isHovered)
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                else
                    RenderSystem.setShaderColor(0.9f, 0.9f, 0.9f, 0.9f);
            } else {
                RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, 0.1f);
            }
            blit(poseStack, x, y, 0, width, height, ParCoolTextureAtlases.action(entry.getActionEntry()));
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        @Override
        public void updateNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public void onPress() {
            setSelectedSkill(this.entry);
        }
    }
}
