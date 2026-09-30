package com.alrex.parcool.client.gui.screen;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.gui.GuiColorPallet;
import com.alrex.parcool.client.gui.components.*;
import com.alrex.parcool.client.textures.ParCoolActionsTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolGuiTextureAtlas;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.network.EnableActionPacket;
import com.alrex.parcool.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ParCoolSettingScreen extends ParCoolTabletScreen {
    public enum SettingTab {
        ACTIONS("parcool.gui.settings.actions", ParCoolGuiTextureAtlas.SETTINGS_ICON_ACTIONS),
        ANIMATIONS("parcool.gui.settings.animations", ParCoolGuiTextureAtlas.SETTINGS_ICON_ANIMATIONS),
        GENERAL("parcool.gui.settings.general", ParCoolGuiTextureAtlas.SETTINGS_ICON_OPTIONS),
        LINKS("parcool.gui.settings.links", ParCoolGuiTextureAtlas.SETTINGS_ICON_LINKS);
        private final String translationKey;
        private final ResourceLocation spriteLocation;

        SettingTab(String translationKey, ResourceLocation spriteLocation) {
            this.translationKey = translationKey;
            this.spriteLocation = spriteLocation;
        }
    }

    private record OptionGroup(Component name, List<BooleanOptionProvider> boolOptions,
                               List<EnumOptionProvider> enumOptions) {
    }

    private record BooleanOptionProvider(Component name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        public static BooleanOptionProvider from(ForgeConfigSpec.BooleanValue config) {
            return new BooleanOptionProvider(Component.translatable(config.getPath().stream().reduce("parcool.config", (a, b) -> a + "." + b)), config, config::set);
        }

        public static BooleanOptionProvider from(Component name, ForgeConfigSpec.BooleanValue config) {
            return new BooleanOptionProvider(name, config, config::set);
        }
    }

    private record EnumOptionProvider<E extends Enum<E>>(Component name, Supplier<E> getter, Consumer<E> setter) {
        public static <E extends Enum<E>> EnumOptionProvider<E> from(ForgeConfigSpec.EnumValue<E> config) {
            return new EnumOptionProvider<>(Component.translatable(config.getPath().stream().reduce("parcool.config", (a, b) -> a + "." + b)), config, config::set);
        }

        public void next() {
            var current = getter.get();
            var enumValues = current.getClass().getEnumConstants();
            setter.accept((E) enumValues[(current.ordinal() + 1) % enumValues.length]);
        }
    }

    private SettingTab currentTab = SettingTab.ACTIONS;
    private EnumMap<SettingTab, AbstractWidget> settingWidgets;
    private final ActionCapabilities enabledActions;

    public ParCoolSettingScreen(ActionCapabilities enabledActions, boolean openedByGuideItem) {
        super(Component.empty(), GuiColorPallet.DEFAULT_DARK, openedByGuideItem);
        this.enabledActions = enabledActions;
    }

    @Override
    protected void initWidgets() {
        var tabWidth = Math.min(120, contentWidth / 4);
        var topBarHeight = font.lineHeight + 10;
        var margin = 10;
        addRenderableOnly(new TexturedPanel(contentOffsetX, contentOffsetY, tabWidth, contentHeight, TexturedPanel.Textures.SETTING_CARD));
        addRenderableWidget(new TabSelectionList(
                minecraft, font, colors, tabWidth - 13, contentHeight - topBarHeight - 1, contentOffsetY + topBarHeight + 1, contentOffsetY + contentHeight - 50
        )).setLeftPos(contentOffsetX + 13);
        addRenderableWidget(new ExtendableSpriteButton.BasicOff(
                font, contentOffsetX + 18, contentOffsetY + contentHeight - 50, tabWidth - 23, 20,
                Component.translatable("parcool.gui.text.open_skilltree"),
                () -> ParCool.PROXY.openSkillTreeGui(minecraft.player, false))
        );
        addRenderableWidget(new ExtendableSpriteButton.BasicOff(
                font, contentOffsetX + 18, contentOffsetY + contentHeight - 25, tabWidth - 23, 20,
                Component.translatable("parcool.gui.text.close"),
                () -> minecraft.setScreen(null))
        );
        settingWidgets = new EnumMap<>(SettingTab.class);
        settingWidgets.put(
                SettingTab.ACTIONS,
                addRenderableWidget(createSkillOptions(contentOffsetX + tabWidth + margin, contentOffsetY + topBarHeight, contentWidth - tabWidth - margin * 2, contentHeight - topBarHeight))
        );
        settingWidgets.put(
                SettingTab.ANIMATIONS,
                addRenderableWidget(createAnimationOptions(contentOffsetX + tabWidth + margin, contentOffsetY + topBarHeight, contentWidth - tabWidth - margin * 2, contentHeight - topBarHeight))
        );
        settingWidgets.put(
                SettingTab.GENERAL,
                addRenderableWidget(createGeneralOptions(contentOffsetX + tabWidth + margin, contentOffsetY + topBarHeight, contentWidth - tabWidth - margin * 2, contentHeight - topBarHeight))
        );
        settingWidgets.put(
                SettingTab.LINKS,
                addRenderableWidget(createLinks(contentOffsetX + tabWidth + margin, contentOffsetY + topBarHeight, contentWidth - tabWidth - margin * 2, contentHeight - topBarHeight))
        );
        addRenderableOnly(new TexturedPanel(contentOffsetX, contentOffsetY, 13, contentHeight, TexturedPanel.Textures.SETTING_SURFACE).withBorder(colors.shadow()));
        addRenderableOnly(new TexturedPanel(contentOffsetX, contentOffsetY, contentWidth, topBarHeight, TexturedPanel.Textures.SETTING_TOP).withBorder(colors.shadow()));
        addRenderableOnly(new TextWidget(font, contentOffsetX, contentOffsetY + 1 + (topBarHeight - font.lineHeight) / 2, tabWidth, Component.translatable("parcool.gui.text.setting"), TextWidget.HorizontalAlignment.CENTER, colors.onPrimary()).withShadow(true));
        changeTab(currentTab);
    }

    private AbstractWidget createSkillOptions(int x, int y, int width, int height) {
        var actions = ParCool.getActionRegistry().getRegisteredActions();
        var widgetList = new ArrayList<AbstractWidget>();
        var widgetY = 8;
        var serverConfig = ParCool.getConfig().server();
        for (var action : actions.values()) {
            if (!action.option().needLearning()) continue;
            if (!serverConfig.get(action).permit().get()) continue;
            ExtendableSpriteToggleButton toggleButton;
            widgetList.add(new WidgetGroup(
                    0, widgetY, width, 36,
                    List.of(
                            new TexturedPanel(1, 0, width - 2, 36, TexturedPanel.Textures.SETTING_CARD).withBorder(colors.separator()),
                            new ImageBySpriteWidget(
                                    4, 2, 32, 32,
                                    ParCoolActionsTextureAtlas.TEXTURE_LOCATION,
                                    ParCoolTextures.action(action)
                            ),
                            new TextWidget(
                                    font, 42, 14, width - 96,
                                    Component.translatable(action.getTranslationKey()),
                                    TextWidget.HorizontalAlignment.START,
                                    colors.onPrimary()
                            ),
                            toggleButton = new ExtendableSpriteToggleButton.Basic(
                                    font, width - 60, 6, 56, 24,
                                    Component.translatable("parcool.gui.text.enabled"),
                                    Component.translatable("parcool.gui.text.disabled"),
                                    (state) -> ParCool.CONNECTION.sendToServer(new EnableActionPacket(action, state))
                            )
                    )
            ));
            toggleButton.setState(enabledActions.can(action));
            widgetY += 48;
        }
        widgetList.trimToSize();
        return new ScrollableWidgetGroup(x, y, width, height, ScrollableWidgetGroup.ScrollType.VERTICAL, Collections.unmodifiableList(widgetList));
    }

    private AbstractWidget createGeneralOptions(int x, int y, int width, int height) {
        var config = ParCool.getConfig().client();
        var options = List.of(
                new OptionGroup(Component.translatable("parcool.config.group.hud"),
                        List.of(
                                BooleanOptionProvider.from(config.staminaHud.showAlways()),
                                BooleanOptionProvider.from(config.staminaHud.hideAutomatically())
                        ),
                        List.of(
                                EnumOptionProvider.from(config.staminaHud.type())
                        )
                ),
                new OptionGroup(Component.translatable("parcool.config.group.grapple"),
                        List.of(
                                BooleanOptionProvider.from(config.showTargetIndicator)
                        ),
                        Collections.emptyList()
                ),
                new OptionGroup(Component.translatable("parcool.config.group.misc"),
                        List.of(
                                BooleanOptionProvider.from(config.enableActionSounds)
                        ),
                        Collections.emptyList()
                )
        );
        return createOptionList(x, y, width, height, options);
    }

    private AbstractWidget createAnimationOptions(int x, int y, int width, int height) {
        var config = AnimationSystemConfig.getInstance();
        var options = List.of(
                new OptionGroup(Component.translatable("parcool.config.group.animation.general"),
                        List.of(
                                BooleanOptionProvider.from(config.enableAnimation),
                                BooleanOptionProvider.from(config.enableCameraAnimation)
                        ),
                        Collections.emptyList()
                ),
                new OptionGroup(Component.translatable("parcool.config.group.animation.availability"),
                        config.getAnimationAvailabilities()
                                .values()
                                .stream()
                                .map(it -> BooleanOptionProvider.from(Component.literal(it.getPath().get(it.getPath().size() - 1)), it))
                                .toList(),
                        Collections.emptyList()
                )
        );
        return createOptionList(x, y, width, height, options);
    }

    private AbstractWidget createLinks(int x, int y, int width, int height) {
        return new ScrollableWidgetGroup(x, y, width, height, ScrollableWidgetGroup.ScrollType.VERTICAL, List.of(
                new WidgetGroup(0, 8, width, 100, List.of(
                        new TexturedPanel(1, 0, width - 2, 100, TexturedPanel.Textures.SETTING_CARD).withBorder(colors.separator())
                ))
        ));
    }

    private AbstractWidget createBooleanConfigRow(int x, int y, int width, int height, BooleanOptionProvider provider) {
        SpriteToggleButton button;
        var widgets = new WidgetGroup(x, y, width, height, List.of(
                new TextWidget(font, 0, (height - font.lineHeight) / 2, width - 44, provider.name, TextWidget.HorizontalAlignment.START, colors.onSurface()),
                button = new SpriteToggleButton.Basic(font, width - 28, 2, provider.setter::accept)
        ));
        button.setState(provider.getter.get());
        return widgets;
    }

    private AbstractWidget createEnumConfigRow(int x, int y, int width, int height, EnumOptionProvider<?> provider) {
        SpriteButton button;
        var widgets = new WidgetGroup(x, y, width, height, List.of(
                new TextWidget(font, 0, (height - font.lineHeight) / 2, width - 44, provider.name, TextWidget.HorizontalAlignment.START, colors.onSurface()),
                button = new ExtendableSpriteButton.BasicOn(font, width - 40, 1, 40, 15, Component.literal(provider.getter.get().name()), null)
        ));
        button.setPressedListener(() -> {
            provider.next();
            button.setMessage(Component.literal(provider.getter.get().name()));
        });
        return widgets;
    }

    private AbstractWidget createOptionList(int x, int y, int width, int height, List<OptionGroup> options) {
        var widgetList = new ArrayList<AbstractWidget>();
        var groupWidgetY = 8;
        for (var optionGroup : options) {
            var subWidgetList = new ArrayDeque<AbstractWidget>();
            subWidgetList.add(new TextWidget(font, 6, 6, width - 12, optionGroup.name, TextWidget.HorizontalAlignment.START, colors.primary()).withShadow(true));
            var subWidgetY = 8 + font.lineHeight;
            final int rowHeight = 20;
            for (var optionItem : optionGroup.boolOptions) {
                subWidgetList.add(createBooleanConfigRow(8, subWidgetY, width - 16, rowHeight, optionItem));
                subWidgetY += rowHeight;
            }
            for (var optionItem : optionGroup.enumOptions) {
                subWidgetList.add(createEnumConfigRow(8, subWidgetY, width - 16, rowHeight, optionItem));
                subWidgetY += rowHeight;
            }
            subWidgetList.addFirst(new TexturedPanel(1, 0, width - 2, subWidgetY, TexturedPanel.Textures.SETTING_CARD).withBorder(colors.separator()));
            widgetList.add(new WidgetGroup(0, groupWidgetY, width, subWidgetY, subWidgetList.stream().toList()));
            groupWidgetY += subWidgetY + 8;
        }
        widgetList.trimToSize();
        return new ScrollableWidgetGroup(x, y, width, height, ScrollableWidgetGroup.ScrollType.VERTICAL, Collections.unmodifiableList(widgetList));
    }

    public void changeTab(SettingTab tab) {
        this.currentTab = tab;
        this.settingWidgets.values().forEach(it -> it.visible = false);
        this.settingWidgets.get(tab).visible = true;
    }

    private class TabSelectionList extends ObjectSelectionList<TabSelectionList.TabEntry> {
        private final GuiColorPallet colors;
        private final Font font;

        public TabSelectionList(Minecraft minecraft, Font font, GuiColorPallet colors, int width, int height, int top, int bottom) {
            super(minecraft, width, height, top, bottom, /*itemHeight*/ 20);
            this.font = font;
            this.colors = colors;
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
            Arrays.stream(SettingTab.values()).map(TabEntry::new).forEach(this::addEntry);
        }

        @Override
        public int getRowWidth() {
            return width;
        }

        @Override
        public int getRowLeft() {
            return getLeft();
        }

        @Override
        protected int getRowTop(int idx) {
            return super.getRowTop(idx) - 2;
        }

        @Override
        public void setSelected(@Nullable TabEntry selected) {
            super.setSelected(selected);
            if (selected != null) changeTab(selected.tab);
        }

        private class TabEntry extends Entry<TabEntry> {
            public TabEntry(SettingTab tab) {
                this.tab = tab;
                this.sprite = ParCoolTextures.guiSprite(tab.spriteLocation);
                this.text = Component.translatable(tab.translationKey);
            }

            private final SettingTab tab;
            private final Component text;
            private final TextureAtlasSprite sprite;

            @Nonnull
            @Override
            public Component getNarration() {
                return Component.empty();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int click) {
                if (click == 0) {
                    TabSelectionList.this.setSelected(this);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void render(@Nonnull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
                if (isMouseOver) {
                    fill(poseStack, left, top - 1, left + width, top + height + 1, ColorUtil.withAlpha(colors.primary(), 0x20));
                }
                RenderSystem.setShaderTexture(0, sprite.atlas().location());
                blit(poseStack, left + 4, top + (height - 9) / 2, 0, 9, 9, sprite);
                font.draw(poseStack, text, left + 17, 0.5f + top + (height - font.lineHeight) / 2f, colors.onSurface());
            }
        }
    }
}
