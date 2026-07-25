package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.gui.components.FlatButton;
import com.alrex.parcool.client.gui.components.ImageWidget;
import com.alrex.parcool.client.gui.components.SkillTreeWidget;
import com.alrex.parcool.client.gui.components.WrappedTextWidget;
import com.alrex.parcool.client.textures.ParCoolTextureAtlases;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

public class SkillTreeScreen extends ParCoolTabletScreen {
    private SkillTreeWidget skilltreeWidget;
    private ImageWidget selectedSkillIconWidget;
    private WrappedTextWidget selectedSkillNameWidget;
    private FlatButton unlockButton;
    private FlatButton viewGuideButton;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    private int skillViewTabOffsetX;
    private int skillViewTabOffsetY;
    private final ActionCapabilities capabilities;
    private final List<SkillTree> trees;

    public SkillTreeScreen(ActionCapabilities capabilities, List<SkillTree> trees) {
        super(Component.empty(), "prcl://skilltree");
        this.trees = trees;
        this.capabilities = capabilities;
    }

    @Override
    protected void init() {
        super.init();
        int skillViewTabWidth = CONTENT_WIDTH - 190;
        skillViewTabOffsetX = contentOffsetX + CONTENT_WIDTH - skillViewTabWidth;
        skillViewTabOffsetY = contentOffsetY;
        skilltreeWidget = addRenderableWidget(
                new SkillTreeWidget(trees, capabilities, contentOffsetX, contentOffsetY, 190, CONTENT_HEIGHT, this::onSkillSelectionChanged)
        );
        selectedSkillIconWidget = addRenderableOnly(
                new ImageWidget(skillViewTabOffsetX + 4, skillViewTabOffsetY + 4, skillViewTabWidth - 8, skillViewTabWidth - 8, null)
        );
        selectedSkillNameWidget = addRenderableOnly(
                new WrappedTextWidget(
                        font,
                        skillViewTabOffsetX + 5,
                        selectedSkillIconWidget.y + selectedSkillIconWidget.getHeight() + 4,
                        skillViewTabWidth - 8,
                        40,
                        Component.empty(),
                        0xFF222222
                )
        );
        unlockButton = addRenderableWidget(
                new FlatButton(
                        font,
                        skillViewTabOffsetX + 4,
                        contentOffsetY + CONTENT_HEIGHT - 21,
                        skillViewTabWidth - 8,
                        Component.literal("Unlock"),
                        0xFF678FE3,
                        0xFFFFFFFF,
                        true,
                        this::unlockSkill
                )
        );
        viewGuideButton = addRenderableWidget(
                new FlatButton(
                        font,
                        skillViewTabOffsetX + 4,
                        contentOffsetY + CONTENT_HEIGHT - 21,
                        skillViewTabWidth - 8,
                        Component.literal("View guide"),
                        0xFF678FE3,
                        0xFFFFFFFF,
                        false,
                        this::viewGuide
                )
        );
        onSkillSelectionChanged(selectedSkill);
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        fill(poseStack, contentOffsetX, contentOffsetY, contentOffsetX + CONTENT_WIDTH, contentOffsetY + CONTENT_HEIGHT, 0xFFBBBBBB);
        fill(poseStack, skillViewTabOffsetX, skillViewTabOffsetY, contentOffsetX + CONTENT_WIDTH, contentOffsetY + CONTENT_HEIGHT, ~0);
        super.renderContent(poseStack, mouseX, mouseY, partial);
        fill(poseStack, skillViewTabOffsetX - 1, skillViewTabOffsetY, skillViewTabOffsetX, contentOffsetY + CONTENT_HEIGHT, 0x88999999);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (skilltreeWidget.isMouseOver(mouseX, mouseY)) {
            skilltreeWidget.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
        }
        return true;
    }

    private void unlockSkill() {
    }

    private void viewGuide() {
    }

    private void onSkillSelectionChanged(@Nullable SkillTree.Entry<?> selectedItem) {
        this.selectedSkill = selectedItem;
        var action = selectedItem != null ? selectedItem.getActionEntry() : null;
        selectedSkillIconWidget.setImage(action != null ? ParCoolTextureAtlases.action(action) : null);
        selectedSkillNameWidget.setMessage(action != null ? Component.translatable(action.getTranslationKey()) : Component.empty());
        if (selectedItem != null) {
            if (selectedItem.isUnlocked(capabilities)) {
                unlockButton.visible = false;
                viewGuideButton.visible = true;
            } else {
                unlockButton.visible = true;
                viewGuideButton.visible = false;
            }
        } else {
            unlockButton.visible = false;
            viewGuideButton.visible = false;
        }
        if (action != null) {
            setTopBarText("prcl://skilltree?a=" + action.id().getNamespace() + "." + action.id().getPath());
        } else {
            setTopBarText("prcl://skilltree");
        }
    }
}
