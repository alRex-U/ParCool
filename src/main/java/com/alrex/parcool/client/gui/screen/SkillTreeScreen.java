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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends ParCoolTabletScreen {
    private static final int SKILL_VIEW_TAB_WIDTH = 56;
    private static final int LEFT_BAR_WIDTH = 13;
    private SkillTreeWidget skilltreeWidget;
    private ImageBySpriteWidget selectedSkillIconWidget;
    private WrappedTextWidget selectedSkillNameWidget;
    private ImageBySpriteButton unlockButton;
    private ImageBySpriteButton viewGuideButton;
    private TextWidget costView;
    private TextWidget currentExperienceLevelView;
    private ToggleActionButton toggleActionButton;
    private WidgetGroup actionUnlockedViewGroup;
    private WidgetGroup actionUnlockStateViewGroup;
    private WidgetGroup skillViewTabGroup;
    private WidgetGroup currentExperienceViewGroup;
    @Nullable
    private SkillTree.Entry<?> selectedSkill;
    private final ActionCapabilities capabilities;
    private final ActionCapabilities enabledActions;
    private final List<SkillTree> trees;
    private int offsetX, offsetY, viewWidth, viewHeight;
    private final boolean openedByGuideItem;
    private boolean fullScreen;
    private final int leftBarWidth;

    public SkillTreeScreen(ActionCapabilities capabilities, ActionCapabilities enabledActions, List<SkillTree> trees, boolean openByGuideItem) {
        super(Component.empty(), GuiColorPallet.DEFAULT_DARK, "prcl://skilltree");
        this.trees = trees;
        this.capabilities = capabilities;
        this.enabledActions = enabledActions;
        this.openedByGuideItem = openByGuideItem;
        this.fullScreen = !openedByGuideItem;
        this.leftBarWidth = this.openedByGuideItem ? LEFT_BAR_WIDTH : 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (fullScreen) {
            offsetX = 0;
            offsetY = 0;
            viewWidth = width;
            viewHeight = height;
        } else {
            offsetX = contentOffsetX;
            offsetY = contentOffsetY;
            viewWidth = CONTENT_WIDTH;
            viewHeight = CONTENT_HEIGHT;
        }
        int skillViewTabOffsetX = offsetX + viewWidth - SKILL_VIEW_TAB_WIDTH;
        int skillViewTabOffsetY = offsetY;
        skilltreeWidget = addRenderableWidget(
                new SkillTreeWidget(trees, capabilities, enabledActions, offsetX + leftBarWidth, offsetY, 190 - leftBarWidth, viewHeight, this::onSkillSelectionChanged)
        );
        skillViewTabGroup = addRenderableWidget(
                new WidgetGroup(
                        skillViewTabOffsetX, skillViewTabOffsetY, offsetX + viewWidth - skillViewTabOffsetX, viewHeight,
                        List.of(
                                new CardPanel(0, 0, SKILL_VIEW_TAB_WIDTH, viewHeight, colors.surface(), colors.shadow()).shadowLeft(true),
                                new CardPanel(2, 3, SKILL_VIEW_TAB_WIDTH - 4, viewHeight - 50, colors.surface(), colors.shadow()).shadowLeft(true).shadowRight(true).shadowTop(true).shadowBottom(true),
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
                                unlockButton = new ImageBySpriteButton(
                                        font, 3, viewHeight - 17, 50, 15,
                                        Component.translatable("parcool.gui.text.unlock"),
                                        colors.onPrimary(),
                                        ParCoolGuiTextureAtlas.TEXTURE_LOCATION,
                                        ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON),
                                        this::unlockSkill
                                ),
                                viewGuideButton = new ImageBySpriteButton(
                                        font, 3, viewHeight - 17, 50, 15,
                                        Component.translatable("parcool.gui.text.open_guide"),
                                        colors.onPrimary(),
                                        ParCoolGuiTextureAtlas.TEXTURE_LOCATION,
                                        ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.BASIC_BUTTON),
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
                                                toggleActionButton = new ToggleActionButton(0, 11)
                                        )
                                )
                        )
                )
        );
        currentExperienceViewGroup = addRenderableWidget(
                new WidgetGroup(
                        skillViewTabOffsetX - 35,
                        offsetY + 3,
                        33, 11,
                        List.of(
                                new ImageBySpriteWidget(0, 0, 33, 11, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.EXPERIENCE_BOX)),
                                new ImageBySpriteWidget(1, 1, 9, 9, ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.ICON_EXPERIENCE)),
                                currentExperienceLevelView = new TextWidget(font, 13, 2, 17, Component.empty(), TextWidget.HorizontalAlignment.END, ~0).withShadow(true)
                        )
                )
        );
        if (openedByGuideItem) {
            addRenderableOnly(new CardPanel(offsetX, offsetY, leftBarWidth, viewHeight, colors.surface(), colors.shadow()));
            addRenderableWidget(fullScreen
                    ? new IconButton.ShrinkDark(offsetX + 1, offsetY + viewHeight - 13, this::shrinkToTabletUi)
                    : new IconButton.ExpandDark(offsetX + 1, offsetY + viewHeight - 13, this::expandFullScreen)
            );
        }
        onSkillSelectionChanged(selectedSkill);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (fullScreen) {
            renderContent(poseStack, mouseX, mouseY, partial);
            return;
        }
        super.render(poseStack, mouseX, mouseY, partial);
    }

    @Override
    protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        fill(poseStack,
                offsetX, offsetY,
                offsetX + viewWidth, offsetY + viewHeight,
                fullScreen ? ColorUtil.withAlpha(colors.background(), 0xE8) : colors.background()
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
            skilltreeWidget.setWidth(viewWidth - SKILL_VIEW_TAB_WIDTH - leftBarWidth);
            skillViewTabGroup.visible = true;
            unlockButton.active = player.experienceLevel >= learnCost;
            toggleActionButton.updateState();
        } else {
            skilltreeWidget.setWidth(viewWidth - leftBarWidth);
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
        if (action != null) {
            setTopBarText("prcl://skilltree?a=" + action.id().getNamespace() + "." + action.id().getPath());
        } else {
            setTopBarText("prcl://skilltree");
        }
        updateWidgetVisibility();
    }

    private void expandFullScreen() {
        fullScreen = true;
        rebuildWidgets();
    }

    private void shrinkToTabletUi() {
        fullScreen = false;
        rebuildWidgets();
    }

    private class ToggleActionButton extends ImageBySpriteButton {
        private boolean on;

        public ToggleActionButton(int x, int y) {
            super(font, x, y, 50, 13, Component.translatable("parcool.gui.text.enabled"), colors.onSurface(), ParCoolGuiTextureAtlas.TEXTURE_LOCATION, ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.TOGGLE_BUTTON_ON), null);
            on = true;
        }

        @Override
        public void onPress() {
            if (selectedSkill == null) return;
            ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), new EnableActionPacket(selectedSkill.getActionEntry(), !on));
        }

        public void updateState() {
            if (selectedSkill == null) return;
            on = enabledActions.can(selectedSkill.getActionEntry());
            if (on) {
                setMessage(Component.translatable("parcool.gui.text.enabled"));
                setSprite(ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.TOGGLE_BUTTON_ON));
            } else {
                setMessage(Component.translatable("parcool.gui.text.disabled"));
                setSprite(ParCoolTextures.guiSprite(ParCoolGuiTextureAtlas.TOGGLE_BUTTON_OFF));
            }
        }
    }
}
