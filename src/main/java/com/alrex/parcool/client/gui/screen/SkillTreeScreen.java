package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.*;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.textures.ParCoolTextureAtlases;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.network.RequestUnlockActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class SkillTreeScreen extends ParCoolTabletScreen {
    private SkillTreeWidget skilltreeWidget;
    private ImageBySpriteWidget selectedSkillIconWidget;
    private WrappedTextWidget selectedSkillNameWidget;
    private FlatButton unlockButton;
    private FlatButton viewGuideButton;
    private ImageWidget experienceIcon;
    private TextWidget costView;
    private TextWidget unlockedText;
    private CardPanel skillViewTab;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    private int skillViewTabOffsetX;
    private final ActionCapabilities capabilities;
    private final List<SkillTree> trees;

    public SkillTreeScreen(ActionCapabilities capabilities, List<SkillTree> trees) {
        super(Component.empty(), GuiColorPallet.DEFAULT_DARK, "prcl://skilltree");
        this.trees = trees;
        this.capabilities = capabilities;
    }

    @Override
    protected void init() {
        super.init();
        int skillViewTabWidth = CONTENT_WIDTH - 190;
        skillViewTabOffsetX = contentOffsetX + CONTENT_WIDTH - skillViewTabWidth;
        int skillViewTabOffsetY = contentOffsetY;
        skilltreeWidget = addRenderableWidget(
                new SkillTreeWidget(trees, capabilities, contentOffsetX, contentOffsetY, 190, CONTENT_HEIGHT, this::onSkillSelectionChanged)
        );
        skillViewTab = addRenderableOnly(new CardPanel(skillViewTabOffsetX, skillViewTabOffsetY, contentOffsetX + CONTENT_WIDTH - skillViewTabOffsetX, CONTENT_HEIGHT, colors.surface()));
        selectedSkillIconWidget = addRenderableOnly(
                new ImageBySpriteWidget(skillViewTabOffsetX + 4, skillViewTabOffsetY + 4, skillViewTabWidth - 8, skillViewTabWidth - 8, null)
        );
        selectedSkillNameWidget = addRenderableOnly(
                new WrappedTextWidget(
                        font,
                        skillViewTabOffsetX + 5,
                        selectedSkillIconWidget.y + selectedSkillIconWidget.getHeight() + 4,
                        skillViewTabWidth - 8,
                        40,
                        Component.empty(),
                        colors.onSurface()
                )
        );
        unlockButton = addRenderableWidget(
                new FlatButton(
                        font,
                        skillViewTabOffsetX + 4,
                        contentOffsetY + CONTENT_HEIGHT - 21,
                        skillViewTabWidth - 8,
                        Component.literal("Unlock"),
                        colors.primary(),
                        colors.onPrimary(),
                        colors.shadow(),
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
                        Component.literal("Guide"),
                        colors.primary(),
                        colors.onPrimary(),
                        colors.shadow(),
                        true,
                        this::viewGuide
                )
        );
        experienceIcon = addRenderableOnly(
                new ImageWidget(
                        skillViewTabOffsetX + 6,
                        viewGuideButton.y - 12,
                        8, 8,
                        TEXTURE_LOCATION,
                        0, 208, 8, 8
                )
        );
        costView = addRenderableOnly(
                new TextWidget(font,
                        experienceIcon.x + experienceIcon.getWidth() + 2,
                        experienceIcon.y,
                        40,
                        Component.empty(),
                        TextWidget.HorizontalAlignment.START, colors.accent()
                )
        );
        unlockedText = addRenderableOnly(
                new TextWidget(font,
                        skillViewTabOffsetX,
                        costView.y,
                        skillViewTabWidth,
                        Component.translatable("parcool.gui.text.unlocked"),
                        TextWidget.HorizontalAlignment.CENTER,
                        colors.accent()
                )
        );
        onSkillSelectionChanged(selectedSkill);
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        fill(poseStack, contentOffsetX, contentOffsetY, contentOffsetX + CONTENT_WIDTH, contentOffsetY + CONTENT_HEIGHT, colors.background());
        super.renderContent(poseStack, mouseX, mouseY, partial);

        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var playerExp = Component.literal(Integer.toString(player.experienceLevel)).withStyle(Style.EMPTY.withColor(colors.accent()));
        var expWidth = font.width(playerExp);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        var textOffset = selectedSkill != null ? skillViewTabOffsetX - expWidth - 2 : contentOffsetX + CONTENT_WIDTH - 2 - expWidth;
        blit(poseStack, textOffset - 14, contentOffsetY + 3, 0, 208, 8, 8);
        font.drawShadow(poseStack, playerExp, textOffset, contentOffsetY + 3.5f, ~0);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (skilltreeWidget.isMouseOver(mouseX, mouseY)) {
            skilltreeWidget.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
        }
        return true;
    }

    private void unlockSkill() {
        if (selectedSkill == null) return;
        ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), new RequestUnlockActionPacket(selectedSkill.getActionEntry()));
    }

    private void viewGuide() {
        if (selectedSkill == null) return;
        Minecraft.getInstance().setScreen(new ParCoolGuideScreen(GuideResourceManager.getLocation(selectedSkill.getActionEntry())));
    }

    private void onSkillSelectionChanged(@Nullable SkillTree.Entry<?> selectedItem) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        this.selectedSkill = selectedItem;
        var action = selectedItem != null ? selectedItem.getActionEntry() : null;
        selectedSkillIconWidget.setImage(action != null ? ParCoolTextureAtlases.action(action) : null);
        selectedSkillNameWidget.setMessage(action != null ? Component.translatable(action.getTranslationKey()) : Component.empty());
        if (selectedItem != null) {
            if (selectedItem.isUnlocked(capabilities)) {
                unlockButton.visible = false;
                experienceIcon.visible = false;
                costView.visible = false;
                unlockedText.visible = true;
                viewGuideButton.visible = true;
            } else {
                unlockButton.visible = true;
                experienceIcon.visible = true;
                costView.visible = true;
                unlockedText.visible = false;
                viewGuideButton.visible = false;
                costView.setMessage(Component.literal(Integer.toString(selectedItem.getActionEntry().option().learningCost())));
                unlockButton.active = player.experienceLevel >= selectedItem.getActionEntry().option().learningCost();
            }
            skillViewTab.visible = true;
            skilltreeWidget.setWidth(190);
        } else {
            skillViewTab.visible = false;
            unlockButton.visible = false;
            viewGuideButton.visible = false;
            experienceIcon.visible = false;
            costView.visible = false;
            unlockedText.visible = false;
            skilltreeWidget.setWidth(CONTENT_WIDTH);
        }
        if (action != null) {
            setTopBarText("prcl://skilltree?a=" + action.id().getNamespace() + "." + action.id().getPath());
        } else {
            setTopBarText("prcl://skilltree");
        }
    }
}
