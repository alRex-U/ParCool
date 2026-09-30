package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.*;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.textures.ParCoolActionsTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.network.EnableActionPacket;
import com.alrex.parcool.common.network.RequestUnlockActionPacket;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends ParCoolTabletScreen {
    private static final int SKILL_VIEW_TAB_WIDTH = 56;
    private SkillTreeWidget skilltreeWidget;
    private ImageBySpriteWidget selectedSkillIconWidget;
    private WrappedTextWidget selectedSkillNameWidget;
    private AbstractButton unlockButton;
    private AbstractButton viewGuideButton;
    private TextWidget costView;
    private TextWidget currentExperienceLevelView;
    private ExtendableSpriteToggleButton toggleActionButton;
    private WidgetGroup actionUnlockedViewGroup;
    private WidgetGroup actionUnlockStateViewGroup;
    private WidgetGroup skillViewTabGroup;
    private WidgetGroup currentExperienceViewGroup;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    private final ActionCapabilities capabilities;
    private final ActionCapabilities enabledActions;
    private final List<SkillTree> trees;

    public SkillTreeScreen(ActionCapabilities capabilities, ActionCapabilities enabledActions, List<SkillTree> trees, boolean openByGuideItem) {
        super(Component.empty(), GuiColorPallet.DEFAULT_DARK, openByGuideItem);
        this.trees = trees;
        this.capabilities = capabilities;
        this.enabledActions = enabledActions;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void initWidgets() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        int skillViewTabOffsetX = contentOffsetX + contentWidth - SKILL_VIEW_TAB_WIDTH;
        int skillViewTabOffsetY = contentOffsetY;
        skilltreeWidget = addRenderableWidget(
                new SkillTreeWidget(trees, capabilities, enabledActions, contentOffsetX, contentOffsetY, contentWidth, contentHeight, this::onSkillSelectionChanged)
        );
        skillViewTabGroup = addRenderableWidget(
                new WidgetGroup(
                        skillViewTabOffsetX, skillViewTabOffsetY, contentOffsetX + contentWidth - skillViewTabOffsetX, contentHeight,
                        List.of(
                                new CardPanel(0, 0, SKILL_VIEW_TAB_WIDTH, contentHeight, colors.surface(), colors.shadow()).shadowLeft(true),
                                new CardPanel(2, 3, SKILL_VIEW_TAB_WIDTH - 4, contentHeight - 50, colors.surface(), colors.shadow()).shadowLeft(true).shadowRight(true).shadowTop(true).shadowBottom(true),
                                selectedSkillIconWidget = new ImageBySpriteWidget(4, 4, SKILL_VIEW_TAB_WIDTH - 8, SKILL_VIEW_TAB_WIDTH - 8, ParCoolActionsTextureAtlas.TEXTURE_LOCATION, null),
                                selectedSkillNameWidget = new WrappedTextWidget(
                                        font,
                                        3,
                                        selectedSkillIconWidget.y + selectedSkillIconWidget.getHeight() + 4,
                                        SKILL_VIEW_TAB_WIDTH - 5,
                                        Component.empty(),
                                        TextWidget.HorizontalAlignment.CENTER,
                                        colors.onSurface()
                                ).withShadow(true),
                                unlockButton = new ExtendableSpriteButton.BasicOn(
                                        font, 3, contentHeight - 17, 50, 15,
                                        Component.translatable("parcool.gui.text.unlock"),
                                        this::unlockSkill
                                ),
                                viewGuideButton = new ExtendableSpriteButton.BasicOn(
                                        font, 3, contentHeight - 17, 50, 15,
                                        Component.translatable("parcool.gui.text.open_guide"),
                                        this::viewGuide
                                ),
                                actionUnlockStateViewGroup = new WidgetGroup(
                                        (SKILL_VIEW_TAB_WIDTH - 50) / 2,
                                        viewGuideButton.y - 13,
                                        50, 11,
                                        List.of(
                                                new ImageBySpriteWidget(0, 0, 50, 11, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.UNLOCK_COST_BOX)),
                                                new ImageBySpriteWidget(1, 1, 9, 9, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.ICON_EXPERIENCE)),
                                                costView = new TextWidget(font, 13, 2, 34, Component.empty(), TextWidget.HorizontalAlignment.END, colors.accent()).withShadow(true)
                                        )
                                ),
                                actionUnlockedViewGroup = new WidgetGroup(
                                        actionUnlockStateViewGroup.x, viewGuideButton.y - 26, 50, 26,
                                        List.of(
                                                new TextWidget(font, 0, 0, 50,
                                                        Component.translatable("parcool.gui.text.unlocked"),
                                                        TextWidget.HorizontalAlignment.CENTER,
                                                        colors.accent()
                                                ).withShadow(true),
                                                toggleActionButton = new ExtendableSpriteToggleButton.Basic(
                                                        font, 0, 11, 50, 13,
                                                        Component.translatable("parcool.gui.text.enabled"),
                                                        Component.translatable("parcool.gui.text.disabled"),
                                                        this::onToggleAction
                                                )
                                        )
                                )
                        )
                )
        );
        currentExperienceViewGroup = addRenderableWidget(
                new WidgetGroup(
                        skillViewTabOffsetX - 35,
                        contentOffsetY + 3,
                        33, 11,
                        List.of(
                                new ImageBySpriteWidget(0, 0, 33, 11, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX)),
                                new ImageBySpriteWidget(1, 1, 9, 9, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.ICON_EXPERIENCE)),
                                currentExperienceLevelView = new TextWidget(font, 13, 2, 17, Component.empty(), TextWidget.HorizontalAlignment.END, ~0).withShadow(true)
                        )
                )
        );
        onSkillSelectionChanged(selectedSkill);
        skilltreeWidget.center(true);
    }

    @Nullable
    @Override
    protected SideBarButtons getSideBarButtons() {
        if (!openedByGuideItem) return new SideBarButtons(Collections.emptyList(), Collections.emptyList());
        return new SideBarButtons(
                Collections.emptyList(),
                List.of(
                        isFullscreen()
                                ? new Tuple<>(IconButton.ShrinkDark::new, () -> this.setFullscreen(false))
                                : new Tuple<>(IconButton.ExpandDark::new, () -> this.setFullscreen(true))
                )
        );
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (skilltreeWidget.isMouseOver(mouseX, mouseY)) {
            skilltreeWidget.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
        }
        return true;
    }

    private void unlockSkill() {
        if (selectedSkill == null) return;
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(ParCoolSoundEvents.SKILLTREE_UNLOCK.get());
        }
        ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), new RequestUnlockActionPacket(selectedSkill.getActionEntry()));
    }

    private void viewGuide() {
        if (selectedSkill == null) return;
        Minecraft.getInstance().setScreen(new ParCoolGuideScreen(GuideResourceManager.getLocation(selectedSkill.getActionEntry())));
    }

    @Override
    protected void onPressTobBarButton() {
        if (selectedSkill != null) onSkillSelectionChanged(null);
        else Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void tick() {
        super.tick();
        updateWidgetVisibility();
    }

    private void updateWidgetVisibility() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var selectedItem = this.selectedSkill;
        if (selectedItem != null) {
            var learnCost = selectedItem.getLearningCost();
            if (selectedItem.isUnlocked(capabilities)) {
                unlockButton.visible = false;
                actionUnlockStateViewGroup.visible = false;
                actionUnlockedViewGroup.visible = true;
                viewGuideButton.visible = true;
                viewGuideButton.active = openedByGuideItem;
            } else {
                unlockButton.visible = true;
                actionUnlockStateViewGroup.visible = true;
                actionUnlockedViewGroup.visible = false;
                viewGuideButton.visible = false;
                costView.setMessage(Component.literal(
                        learnCost + "/" + (player.experienceLevel < 100 ? Integer.toString(player.experienceLevel) : "99+")
                ).withStyle(Style.EMPTY.withColor(player.experienceLevel >= learnCost ? colors.accent() : colors.onSurface())));
            }
            skilltreeWidget.setWidth(contentWidth - SKILL_VIEW_TAB_WIDTH);
            skillViewTabGroup.visible = true;
            unlockButton.active = player.experienceLevel >= learnCost;
            toggleActionButton.setState(enabledActions.can(selectedSkill.getActionEntry()));
        } else {
            skilltreeWidget.setWidth(contentWidth);
            skillViewTabGroup.visible = false;
        }
        currentExperienceLevelView.setMessage(Component.literal(player.experienceLevel < 100 ? Integer.toString(player.experienceLevel) : "99+").withStyle(Style.EMPTY.withColor(colors.accent())));
        currentExperienceViewGroup.x = skilltreeWidget.x + skilltreeWidget.getWidth() - 35;
        currentExperienceViewGroup.visible = ParCool.getConfig().server().enableSkillTree.get();
    }

    private void onSkillSelectionChanged(@Nullable SkillTree.Entry<?> selectedItem) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        this.selectedSkill = selectedItem;
        var action = selectedItem != null ? selectedItem.getActionEntry() : null;
        selectedSkillIconWidget.setImage(action != null ? ParCoolTextures.action(action) : null);
        selectedSkillNameWidget.setMessage(action != null ? Component.translatable(action.getTranslationKey()) : Component.empty());
        if (selectedSkill == null)
            setTopBarText(Component.translatable("parcool.gui.top.skilltree"));
        else
            setTopBarText(Component.translatable("parcool.gui.top.skilltree.skill", I18n.get(selectedSkill.getActionEntry().getTranslationKey())));
        updateWidgetVisibility();
    }

    private void onToggleAction(boolean state) {
        if (selectedSkill == null) return;
        ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), new EnableActionPacket(selectedSkill.getActionEntry(), state));
    }
}
