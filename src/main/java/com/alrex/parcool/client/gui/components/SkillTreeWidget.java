package com.alrex.parcool.client.gui.components;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.gui.GuiRenderUtil;
import com.alrex.parcool.client.textures.ParCoolActionsTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class SkillTreeWidget extends AbstractWidget {
    private final List<SkillTreeActionIcon> icons;
    private final List<ConnectivityWidget> connectivities;
    private final ActionCapabilities capabilities;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    @Nullable
    private final Consumer<SkillTree.Entry<?>> selectionListener;
    private double scrollX;
    private double scrollY;
    private float scale = 1f;
    private final int contentWidth;
    private final int contentHeight;

    public SkillTreeWidget(List<SkillTree> skillTrees, ActionCapabilities capabilities, int x, int y, int width, int height, @Nullable Consumer<SkillTree.Entry<?>> selectionListener) {
        super(x, y, width, height, Component.empty());
        this.capabilities = capabilities;
        this.selectionListener = selectionListener;
        this.icons = new ArrayList<>();
        this.connectivities = new ArrayList<>();
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
        widget.setX(xOffset + (width - widget.getWidth()) / 2); // centering the widget to children
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
        return scrollX + (mouseX - getX()) / scale;
    }

    private double getMouseYInContent(double mouseY) {
        return scrollY + (mouseY - getY()) / scale;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var poseStack = graphics.pose();
        boolean mouseIsOutOfWidget = mouseX < getX() || getX() + width < mouseX || mouseY < getY() || getY() + height < mouseY;
        poseStack.pushPose();
        {
            GuiRenderUtil.enableScissorTestInGuiCoordinate(getX(), getY(), width, height);
            int mouseXScaled = mouseIsOutOfWidget ? -1 : (int) getMouseXInContent(mouseX);
            int mouseYScaled = mouseIsOutOfWidget ? -1 : (int) getMouseYInContent(mouseY);

            poseStack.translate(getX(), getY(), 0);
            poseStack.scale(scale, scale, 0);
            poseStack.translate(-scrollX, -scrollY, 0);
            for (var widget : connectivities) {
                widget.render(graphics, mouseXScaled, mouseYScaled, partialTick);
            }
            for (var widget : icons) {
                widget.render(graphics, mouseXScaled, mouseYScaled, partialTick);
            }
            RenderSystem.disableScissor();
        }
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scale += (float) scrollY / 4f;
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

    private class ConnectivityWidget implements Renderable {
        private final SkillTreeActionIcon root;
        private final List<SkillTreeActionIcon> leaves;
        private final int yOfHLine;

        public ConnectivityWidget(SkillTreeActionIcon root, List<SkillTreeActionIcon> leaves) {
            this.root = root;
            this.leaves = Collections.unmodifiableList(leaves);
            var rootBottom = root.getY() + root.getHeight();
            var leafTop = 1024;
            for (var leaf : leaves) {
                if (leafTop > leaf.getY()) leafTop = leaf.getY();
            }
            yOfHLine = (rootBottom + leafTop) / 2;
        }

        @Override
        public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int color = root.getSkillTreeEntry().isUnlocked(capabilities) ? 0xFFEEEEEE : 0xAA777777;
            var rootYCenter = root.getY() + root.getHeight() / 2;
            var rootXCenter = root.getX() + root.getWidth() / 2;
            if (leaves.isEmpty()) return;
            if (leaves.size() == 1) {
                graphics.fill(rootXCenter - 1, rootYCenter, rootXCenter + 2, leaves.get(0).getY(), 0xFF000000);
                graphics.vLine(rootXCenter, rootYCenter, leaves.get(0).getY(), color);
                return;
            }
            int minX = leaves.stream().map(leaf -> leaf.getX() + leaf.getWidth() / 2).min(Integer::compare).get();
            int maxX = leaves.stream().map(leaf -> leaf.getX() + leaf.getWidth() / 2).max(Integer::compare).get();
            graphics.fill(minX - 1, yOfHLine - 1, maxX + 1, yOfHLine + 2, 0xFF000000);
            graphics.hLine(minX, maxX, yOfHLine, color);
            for (var leaf : leaves) {
                var x = leaf.getX() + leaf.getWidth() / 2;
                var y = leaf.getY() + leaf.getHeight() / 2;
                graphics.fill(x - 1, yOfHLine + 1, x + 2, y, 0xFF000000);
                graphics.vLine(x, yOfHLine, y, color);
            }
            graphics.fill(rootXCenter - 1, rootYCenter, rootXCenter + 2, yOfHLine, 0xFF000000);
            graphics.vLine(rootXCenter, rootYCenter, yOfHLine, color);
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
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShaderTexture(0, ParCoolGuiTextureAtlas.TEXTURE_LOCATION);
            if (entry.isVisible(capabilities)) {
                if (isHovered)
                    RenderSystem.setShaderColor(0.9f, 0.9f, 0.9f, 1f);
                else
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                graphics.blit(this.getX(), this.getY(), 0, this.width, this.height,
                        ParCoolTextures.guiSprite(entry.isUnlocked(capabilities)
                                ? ParCoolGuiTextureAtlas.SKILLTREE_ACTION_UNLOCKED
                                : ParCoolGuiTextureAtlas.SKILLTREE_ACTION_LOCKED
                        )
                );
                if (visible) {
                    RenderSystem.setShaderTexture(0, ParCoolActionsTextureAtlas.TEXTURE_LOCATION);
                    graphics.blit(this.getX(), this.getY(), 0, this.width, this.height, ParCoolTextures.action(entry.getActionEntry()));
                }
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            } else {
                graphics.blit(this.getX(), this.getY(), 0, this.width, this.height, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.SKILLTREE_ACTION_UNAVAILABLE));
            }
        }

        @Override
        protected boolean isValidClickButton(int p_93652_) {
            return entry.isVisible(capabilities);
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public void onPress() {
            setSelectedSkill(this.entry);
        }
    }
}
