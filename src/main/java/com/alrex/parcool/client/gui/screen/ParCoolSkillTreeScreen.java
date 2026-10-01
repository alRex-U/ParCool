package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.*;
import com.alrex.parcool.client.textures.ParCoolActionsTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.network.RequestUnlockActionPacket;
import com.alrex.parcool.common.skilltree.SkillTree;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ParCoolSkillTreeScreen extends ParCoolTabletScreen {
    private static final int SKILL_VIEW_CARD_WIDTH = 72;
    private static final int SKILL_VIEW_CARD_HEIGHT = (int) (SKILL_VIEW_CARD_WIDTH * 2.5);
    private ImageBySpriteWidget selectedSkillIconWidget;
    private WrappedTextWidget selectedSkillNameWidget;
    private AbstractButton unlockButton;
    private ExperienceProgressView costView;
    private ExperienceProgressView currentExperienceLevelView;
    private WidgetGroup actionWhenUnlockedViewGroup;
    private WidgetGroup actionWhenLockedViewGroup;
    private WidgetGroup skillViewCardGroup;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    private final ActionCapabilities capabilities;
    private final ActionCapabilities enabledActions;
    private final List<SkillTree> trees;

    public ParCoolSkillTreeScreen(ActionCapabilities capabilities, ActionCapabilities enabledActions, List<SkillTree> trees) {
        super(Component.empty(), GuiColorPallet.DEFAULT_DARK, false);
        this.trees = trees;
        this.capabilities = capabilities;
        this.enabledActions = enabledActions;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        updateWidgetVisibility();
    }

    @Override
    protected void initWidgets() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var skillTreeOffsetY = this.contentOffsetY;
        var skillTreeHeight = this.contentHeight;
        int skillViewTabOffsetX = contentOffsetX + contentWidth - SKILL_VIEW_CARD_WIDTH - 5;
        int skillViewTabOffsetY = skillTreeOffsetY + (skillTreeHeight - SKILL_VIEW_CARD_HEIGHT) / 2;
        addRenderableOnly(new TexturedPanel(contentOffsetX, skillTreeOffsetY, contentWidth, skillTreeHeight, TexturedPanel.Textures.SKILLTREE_BACKGROUND));
        var skilltreeWidget = addRenderableWidget(
                new SkillTreeWidget(trees, capabilities, enabledActions, contentOffsetX, skillTreeOffsetY, contentWidth, skillTreeHeight, this::onSkillSelectionChanged)
        );
        skillViewCardGroup = addRenderableWidget(
                new WidgetGroup(
                        skillViewTabOffsetX, skillViewTabOffsetY, SKILL_VIEW_CARD_WIDTH, SKILL_VIEW_CARD_HEIGHT,
                        List.of(
                                new TexturedPanel(0, 0, SKILL_VIEW_CARD_WIDTH, SKILL_VIEW_CARD_HEIGHT, TexturedPanel.Textures.SKILLTREE_SURFACE).withBorder(colors.shadow()),
                                new CardPanel(3, 3, SKILL_VIEW_CARD_WIDTH - 6, SKILL_VIEW_CARD_HEIGHT - 54, 0, colors.shadow()).shadowAll(true),
                                selectedSkillIconWidget = new ImageBySpriteWidget(4, 4, SKILL_VIEW_CARD_WIDTH - 8, SKILL_VIEW_CARD_WIDTH - 8, ParCoolActionsTextureAtlas.TEXTURE_LOCATION, null),
                                selectedSkillNameWidget = new WrappedTextWidget(
                                        font,
                                        3,
                                        selectedSkillIconWidget.y + selectedSkillIconWidget.getHeight() + 4,
                                        SKILL_VIEW_CARD_WIDTH - 5,
                                        Component.empty(), TextWidget.HorizontalAlignment.CENTER, colors.onSurface()
                                ).withShadow(true),
                                actionWhenLockedViewGroup = new WidgetGroup(
                                        0, SKILL_VIEW_CARD_HEIGHT - 49, SKILL_VIEW_CARD_WIDTH, 49,
                                        List.of(
                                                costView = new ExperienceProgressView(font, 3, 3, Component.empty()),
                                                unlockButton = new ExtendableSpriteButton.BasicOn(
                                                        font, 3, 20, SKILL_VIEW_CARD_WIDTH - 6, 26,
                                                        Component.translatable("parcool.gui.text.unlock"), this::unlockSkill
                                                )
                                        )
                                ),
                                actionWhenUnlockedViewGroup = new WidgetGroup(
                                        0, SKILL_VIEW_CARD_HEIGHT - 49, SKILL_VIEW_CARD_WIDTH, 49,
                                        List.of(
                                                new TextWidget(font, 0, (45 - font.lineHeight) / 2, SKILL_VIEW_CARD_WIDTH,
                                                        Component.translatable("parcool.gui.text.unlocked"), TextWidget.HorizontalAlignment.CENTER, colors.accent()
                                                ).withShadow(true)
                                        )
                                )
                        )
                )
        );
        currentExperienceLevelView = addRenderableOnly(new ExperienceProgressView(font, contentOffsetX + contentWidth - 70, skillTreeOffsetY + 5, Component.empty()));
        addRenderableWidget(new ExtendableSpriteButton.BasicOn(
                font, contentOffsetX + 5, skillTreeOffsetY + skillTreeHeight - 30, 60, 25,
                Component.translatable("parcool.gui.text.open_setting"), () -> ParCool.PROXY.openSettingGui(minecraft.player))
        );
        var title = Component.translatable("parcool.gui.text.skilltree");
        var titleWidth = Math.max(80, font.width(title) + 12);
        addRenderableOnly(new WidgetGroup(contentOffsetX + 6, contentOffsetY + 6, titleWidth, 7 + font.lineHeight, List.of(
                new TexturedPanel(0, 0, titleWidth, 7 + font.lineHeight, TexturedPanel.Textures.SKILLTREE_SURFACE).withBorder(colors.separator()),
                new TextWidget(font, 0, 4, titleWidth,
                        title, TextWidget.HorizontalAlignment.CENTER,
                        colors.onBackground()
                ).withShadow(true)
        )));
        onSkillSelectionChanged(selectedSkill);
        skilltreeWidget.center(true);
    }

    @Override
    public void renderBackground(@Nullable PoseStack poseStack) {
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        fill(poseStack,
                contentOffsetX, contentOffsetY,
                contentOffsetX + contentWidth, contentOffsetY + contentHeight,
                isFullscreen() ? ColorUtil.withAlpha(colors.background(), 0xE8) : colors.background()
        );
        super.renderContent(poseStack, mouseX, mouseY, partial);
    }

    private void unlockSkill() {
        if (selectedSkill == null) return;
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(ParCoolSoundEvents.SKILLTREE_UNLOCK.get());
        }
        ParCool.getConnection().send(PacketDistributor.SERVER.noArg(), new RequestUnlockActionPacket(selectedSkill.getActionEntry()));
    }

    @Override
    protected void onPressTobBarButton() {
        if (selectedSkill != null) onSkillSelectionChanged(null);
        else Minecraft.getInstance().setScreen(null);
    }

    private void updateWidgetVisibility() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var selectedItem = this.selectedSkill;
        if (selectedItem != null) {
            var learnCost = selectedItem.getLearningCost();
            if (selectedItem.isUnlocked(capabilities)) {
                actionWhenLockedViewGroup.visible = false;
                actionWhenUnlockedViewGroup.visible = true;
            } else {
                actionWhenLockedViewGroup.visible = true;
                actionWhenUnlockedViewGroup.visible = false;
                costView.setMessage(Component.literal(
                        (player.experienceLevel < 1000 ? Integer.toString(player.experienceLevel) : "999+") + "/" + learnCost
                ).withStyle(Style.EMPTY.withColor(player.experienceLevel >= learnCost ? 0xFFCDF263 : colors.onSurface())));
                costView.setProgress(player.experienceLevel / (float) learnCost);
            }
            skillViewCardGroup.visible = true;
            unlockButton.active = player.experienceLevel >= learnCost;
        } else {
            skillViewCardGroup.visible = false;
        }
        currentExperienceLevelView.setMessage(Component.literal(player.experienceLevel < 1000 ? Integer.toString(player.experienceLevel) : "999+").withStyle(Style.EMPTY.withColor(0xFFCDF263)));
    }

    private void onSkillSelectionChanged(@Nullable SkillTree.Entry<?> selectedItem) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        this.selectedSkill = selectedItem;
        var action = selectedItem != null ? selectedItem.getActionEntry() : null;
        selectedSkillIconWidget.setImage(action != null ? ParCoolTextures.action(action) : null);
        selectedSkillNameWidget.setMessage(action != null ? Component.translatable(action.getTranslationKey()) : Component.empty());
        updateWidgetVisibility();
    }
}
