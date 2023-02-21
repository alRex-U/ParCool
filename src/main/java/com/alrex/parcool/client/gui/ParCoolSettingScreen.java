package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.info.ActionInfo;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static com.alrex.parcool.ParCoolConfig.CONFIG_CLIENT;

public class ParCoolSettingScreen extends Screen {
	private final ConfigSet[] configItemList = new ConfigSet[]{
			new ConfigSet("Activate ParCool", CONFIG_CLIENT.parCoolActivation::set, CONFIG_CLIENT.parCoolActivation::get),
			new ConfigSet("Infinite stamina", CONFIG_CLIENT.infiniteStamina::set, CONFIG_CLIENT.infiniteStamina::get),
			new ConfigSet("Enable actions needing Fast-Run with normal sprint", CONFIG_CLIENT.substituteSprintForFastRun::set, CONFIG_CLIENT.substituteSprintForFastRun::get),
			new ConfigSet("Always do Fast-Run when doing sprint", CONFIG_CLIENT.replaceSprintWithFastRun::set, CONFIG_CLIENT.replaceSprintWithFastRun::get),
			new ConfigSet("Hide stamina HUD", CONFIG_CLIENT.hideStaminaHUD::set, CONFIG_CLIENT.hideStaminaHUD::get),
			new ConfigSet("Use light Stamina HUD", CONFIG_CLIENT.useLightHUD::set, CONFIG_CLIENT.useLightHUD::get),
			new ConfigSet("Disable a camera rotation of Rolling", CONFIG_CLIENT.disableCameraRolling::set, CONFIG_CLIENT.disableCameraRolling::get),
			new ConfigSet("Disable a camera rotation of Flipping", CONFIG_CLIENT.disableCameraFlipping::set, CONFIG_CLIENT.disableCameraFlipping::get),
			new ConfigSet("Disable a camera animation of Horizontal Wall-Run", CONFIG_CLIENT.disableCameraHorizontalWallRun::set, CONFIG_CLIENT.disableCameraHorizontalWallRun::get),
			new ConfigSet("Disable a camera animation of Vault", CONFIG_CLIENT.disableCameraVault::set, CONFIG_CLIENT.disableCameraVault::get),
			new ConfigSet("Disable double-tapping for dodge", CONFIG_CLIENT.disableDoubleTappingForDodge::set, CONFIG_CLIENT.disableDoubleTappingForDodge::get),
			new ConfigSet("Disable crawl in air", CONFIG_CLIENT.disableCrawlInAir::set, CONFIG_CLIENT.disableCrawlInAir::get),
			new ConfigSet("Disable vault in air", CONFIG_CLIENT.disableVaultInAir::set, CONFIG_CLIENT.disableVaultInAir::get),
			new ConfigSet("Disable falling animation", CONFIG_CLIENT.disableFallingAnimation::set, CONFIG_CLIENT.disableFallingAnimation::get),
			new ConfigSet("Disable all animations", CONFIG_CLIENT.disableAnimation::set, CONFIG_CLIENT.disableAnimation::get),
			new ConfigSet("Disable first person view animations", CONFIG_CLIENT.disableFPVAnimation::set, CONFIG_CLIENT.disableFPVAnimation::get),
			new ConfigSet("Enable roll when player is creative", CONFIG_CLIENT.enableRollWhenCreative::set, CONFIG_CLIENT.enableRollWhenCreative::get)
	};

	private enum SettingMode {Actions, Configs, Limitations}

	private SettingMode mode = SettingMode.Actions;
	private final CheckboxButton[] configButtons = new CheckboxButton[configItemList.length];
	private final ActionConfigSet[] actionList = new ActionConfigSet[ActionList.ACTIONS.size()];
	private final InfoSet[] infoList;
	private final CheckboxButton[] actionButtons = new CheckboxButton[actionList.length];
	private final ModeSet[] modeMenuList = new ModeSet[]{
			new ModeSet(new TranslationTextComponent("parcool.gui.text.action"), SettingMode.Actions, this),
			new ModeSet(new TranslationTextComponent("parcool.gui.text.config"), SettingMode.Configs, this),
			new ModeSet(new TranslationTextComponent("parcool.gui.text.limitation"), SettingMode.Limitations, this)
	};

	public ParCoolSettingScreen(ITextComponent titleIn, ActionInfo info) {
		super(titleIn);
		for (int i = 0; i < actionList.length; i++) {
			actionList[i] = new ActionConfigSet(ActionList.getByIndex(i), info);
			actionButtons[i] = new CheckboxButton(0, 0, 0, Checkbox_Item_Height, new StringTextComponent(actionList[i].name), actionList[i].getter.getAsBoolean());
		}
		for (int i = 0; i < configItemList.length; i++) {
			configButtons[i] = new CheckboxButton(0, 0, 0, Checkbox_Item_Height, new TranslationTextComponent(configItemList[i].name), configItemList[i].getter.getAsBoolean());
		}
		infoList = new InfoSet[]{
				new InfoSet(
						"Max Stamina Limitation",
						Integer.toString(info.getMaxStaminaLimitation())
				),
				new InfoSet(
						"Infinite Stamina Permission",
						Boolean.toString(info.isInfiniteStaminaPermitted())
				)
		};
	}

	private int topIndex = 0;
	private int viewableItemCount = 0;
	private static final int Checkbox_Item_Height = 21;
	private static final int Header_Color1 = 0xBB000040;
	private static final int Header_Color2 = 0xBB400040;
	private static final int Back_Color = 0xCC101020;
	private static final int TopBar_Color1 = 0xFFAA33AA;
	private static final int TopBar_Color2 = 0xFF3333AA;
	private static final int Separator_Color = 0xFFCCCCCC;
	private static final int SubSeparator_Color = 0xFF808080;
	private static final int Text_Color = 0xFFEEEEEE;
	private static final int SubText_Color = 0xFFAAAAAA;

	@Override
	public void onClose() {
		for (int i = 0; i < configItemList.length; i++) {
			configItemList[i].setter.accept(configButtons[i].selected());
		}
		for (int i = 0; i < actionList.length; i++) {
			actionList[i].setter.accept(actionButtons[i].selected());
		}
		super.onClose();
	}

	@Override
	public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
		super.resize(p_231152_1_, p_231152_2_, p_231152_3_);
		mouseScrolled(0, 0, 0);
	}

	private static final ITextComponent MenuTitle = new TranslationTextComponent("parcool.gui.title.setting");

	@Override
	public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
		super.render(matrixStack, mouseX, mouseY, p_230430_4_);
		renderBackground(matrixStack, 0);
		int topBarHeight = font.lineHeight * 2;
		int topBarItemWidth = (int) (1.6 * Arrays.stream(modeMenuList).map(it -> font.width(it.title)).max(Integer::compareTo).orElse(0));
		int topBarOffsetX = width - topBarItemWidth * modeMenuList.length;
		fillGradient(matrixStack, 0, 0, this.width, topBarHeight, TopBar_Color1, TopBar_Color2);
		drawString(
				matrixStack, font, MenuTitle,
				10,
				topBarHeight / 4 + 1,
				Text_Color
		);
		for (int i = 0; i < modeMenuList.length; i++) {
			ModeSet item = modeMenuList[i];
			item.y = 0;
			item.x = topBarOffsetX + i * topBarItemWidth;
			item.width = topBarItemWidth;
			item.height = topBarHeight;
			boolean selected = (item.mode == mode) || item.isMouseIn(mouseX, mouseY);
			drawCenteredString(
					matrixStack, font, item.title,
					topBarOffsetX + i * topBarItemWidth + topBarItemWidth / 2,
					topBarHeight / 4 + 1,
					selected ? Text_Color : SubText_Color
			);
			fill(matrixStack, item.x, 2, item.x + 1, topBarHeight - 3, Separator_Color);
		}
		fill(matrixStack, 0, topBarHeight - 1, width, topBarHeight, Separator_Color);
		switch (mode) {
			case Actions:
				renderActions(matrixStack, mouseX, mouseY, p_230430_4_, topBarHeight);
				break;
			case Configs:
				renderConfigs(matrixStack, mouseX, mouseY, p_230430_4_, topBarHeight);
				break;
			case Limitations:
				renderLimitations(matrixStack, mouseX, mouseY, p_230430_4_, topBarHeight);
		}
	}

	private static final ITextComponent Header_ActionName = new TranslationTextComponent("parcool.gui.text.actionName");
	private static final ITextComponent Header_ServerPermission = new StringTextComponent("G");
	private static final ITextComponent Header_ServerPermissionText = new TranslationTextComponent("parcool.gui.text.globalPermission");
	private static final ITextComponent Header_IndividualPermission = new StringTextComponent("I");
	private static final ITextComponent Header_IndividualPermissionText = new TranslationTextComponent("parcool.gui.text.individualPermission");
	private static final ITextComponent Permission_Permitted = new StringTextComponent("✓");
	private static final ITextComponent Permission_Denied = new StringTextComponent("×");

	private void renderActions(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40, headerHeight = (int) (font.lineHeight * 1.5f);
		int headerOffsetY = offsetY + font.lineHeight * 2;
		int contentOffsetY = headerOffsetY + headerHeight + 2;
		int permissionColumnWidth = font.width(Permission_Permitted) * 5;
		int nameColumnWidth = width - offsetX * 2 - permissionColumnWidth * 2;
		int contentHeight = height - contentOffsetY - font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		int headerTextY = headerOffsetY + headerHeight / 2 - font.lineHeight / 2 + 1;
		drawString(matrixStack, font, Header_ActionName, offsetX + 5, headerTextY, Text_Color);
		drawCenteredString(matrixStack, font, Header_ServerPermission, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, Text_Color);
		drawCenteredString(matrixStack, font, Header_IndividualPermission, offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2, headerTextY, Text_Color);
		for (CheckboxButton actionButton : actionButtons) {
			actionButton.setWidth(0);
		}
		for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
			CheckboxButton button = actionButtons[i + topIndex];
			button.x = offsetX + 1;
			button.y = contentOffsetY + Checkbox_Item_Height * i;
			button.setWidth(nameColumnWidth - 5);
			button.setHeight(20);
			button.render(matrixStack, mouseX, mouseY, p_230430_4_);
			fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, SubSeparator_Color);
			int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
			boolean permitted = actionList[i].serverWideLimitation.getAsBoolean();
			drawCenteredString(
					matrixStack, font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
			permitted = actionList[i].individualLimitation.getAsBoolean();
			drawCenteredString(
					matrixStack, font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
		}
		fillGradient(matrixStack, 0, offsetY, width, headerOffsetY, Header_Color1, Header_Color2);
		fillGradient(matrixStack, 0, contentOffsetY + contentHeight, width, height, Header_Color1, Header_Color2);
		drawCenteredString(matrixStack, font, modeMenuList[0].title, width / 2, offsetY + font.lineHeight / 2 + 2, 0x8888FF);
		if (topIndex + viewableItemCount < actionButtons.length)
			drawCenteredString(matrixStack, font, new StringTextComponent("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, SubText_Color);
		//draw separators
		fill(matrixStack, offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, Separator_Color);
		fill(matrixStack, offsetX, headerOffsetY, offsetX + 1, contentOffsetY + contentHeight, Separator_Color);
		fill(matrixStack, offsetX + nameColumnWidth, headerOffsetY, offsetX + nameColumnWidth + 1, contentOffsetY + contentHeight, Separator_Color);
		fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth + 1, contentOffsetY + contentHeight, Separator_Color);
		fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth * 2, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth * 2 + 1, contentOffsetY + contentHeight, Separator_Color);
		{// draw tooltip
			int columnCenter = offsetX + nameColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				renderComponentTooltip(matrixStack, Collections.singletonList(Header_ServerPermissionText), mouseX, mouseY);
			}

			columnCenter = offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				renderComponentTooltip(matrixStack, Collections.singletonList(Header_IndividualPermissionText), mouseX, mouseY);
			}
		}
	}

	private void renderConfigs(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40;
		int contentWidth = width - offsetX * 2;
		int contentHeight = height - offsetY - font.lineHeight * 4;
		int contentOffsetY = offsetY + font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		for (CheckboxButton configButton : configButtons) {
			configButton.setWidth(0);
		}
		for (int i = 0; i < viewableItemCount && i + topIndex < configItemList.length; i++) {
			CheckboxButton button = configButtons[i + topIndex];
			button.x = offsetX + 1;
			button.y = offsetY + font.lineHeight * 2 + Checkbox_Item_Height * i;
			button.setWidth(contentWidth);
			button.setHeight(20);
			button.render(matrixStack, mouseX, mouseY, p_230430_4_);
			fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, SubSeparator_Color);
		}
		fill(matrixStack, width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, Separator_Color);
		fill(matrixStack, offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, Separator_Color);
		fillGradient(matrixStack, 0, offsetY, width, offsetY + font.lineHeight * 2, Header_Color1, Header_Color2);
		fillGradient(matrixStack, 0, offsetY + contentHeight + font.lineHeight * 2, width, height, Header_Color1, Header_Color2);
		drawCenteredString(matrixStack, font, modeMenuList[1].title, width / 2, offsetY + font.lineHeight / 2 + 2, 0x8888FF);
		if (topIndex + viewableItemCount < configButtons.length)
			drawCenteredString(matrixStack, font, new StringTextComponent("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, SubText_Color);
	}

	private void renderLimitations(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40;
		int contentHeight = height - offsetY - font.lineHeight * 4;
		int contentOffsetY = offsetY + font.lineHeight * 2;
		int itemHeight = font.lineHeight * 2;
		int valueWidth = Arrays.stream(infoList).map(it -> font.width(it.value)).max(Integer::compareTo).orElse(0);
		for (int i = 0; i < infoList.length; i++) {
			InfoSet item = infoList[i];
			drawString(
					matrixStack, font,
					item.name,
					offsetX + 5,
					contentOffsetY + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
					Text_Color
			);
			drawString(
					matrixStack, font,
					item.value,
					width - offsetX - 5 - valueWidth,
					contentOffsetY + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
					Text_Color
			);
			fill(matrixStack, offsetX, contentOffsetY + itemHeight * (i + 1), width - offsetX, contentOffsetY + itemHeight * (i + 1) + 1, SubSeparator_Color);
		}
		fill(matrixStack, width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, Separator_Color);
		fill(matrixStack, offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, Separator_Color);
		fillGradient(matrixStack, 0, offsetY, width, offsetY + font.lineHeight * 2, Header_Color1, Header_Color2);
		fillGradient(matrixStack, 0, offsetY + contentHeight + font.lineHeight * 2, width, height, Header_Color1, Header_Color2);
		drawCenteredString(matrixStack, font, modeMenuList[2].title, width / 2, offsetY + font.lineHeight / 2 + 2, 0x8888FF);
	}

	@Override
	public void renderBackground(@Nonnull MatrixStack p_238651_1_, int p_238651_2_) {
		fill(p_238651_1_, 0, 0, this.width, this.height, Back_Color);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, p_238651_1_));
	}

	@Override
	public boolean mouseScrolled(double x, double y, double value) {
		topIndex -= (int) Math.signum(value);
		switch (mode) {
			case Actions:
				if (topIndex > actionButtons.length - viewableItemCount)
					topIndex = actionButtons.length - viewableItemCount;
				break;
			case Configs:
				if (topIndex > configButtons.length - viewableItemCount)
					topIndex = configButtons.length - viewableItemCount;
		}
		if (topIndex < 0) topIndex = 0;
		return true;
	}

	private static class ModeSet {
		final ITextComponent title;
		final SettingMode mode;
		final ParCoolSettingScreen parent;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		ModeSet(ITextComponent title, SettingMode mode, ParCoolSettingScreen parent) {
			this.title = title;
			this.mode = mode;
			this.parent = parent;
		}

		void set() {
			parent.topIndex = 0;
			parent.mode = mode;
		}

		boolean isMouseIn(int mouseX, int mouseY) {
			return (x < mouseX && mouseX < x + width) && (y < mouseY && mouseY < y + height);
		}
	}

	private static class ConfigSet {
		final String name;
		final Consumer<Boolean> setter;
		final BooleanSupplier getter;

		ConfigSet(String name, Consumer<Boolean> setter, BooleanSupplier getter) {
			this.name = name;
			this.getter = getter;
			this.setter = setter;
		}
	}

	private static class InfoSet {
		final String name;
		final String value;

		InfoSet(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private static class ActionConfigSet {
		final String name;
		final Consumer<Boolean> setter;
		final BooleanSupplier getter;
		final BooleanSupplier serverWideLimitation;
		final BooleanSupplier individualLimitation;

		ActionConfigSet(Class<? extends Action> action, ActionInfo info) {
			name = new TranslationTextComponent("parcool.action." + action.getSimpleName()).getString();
			ForgeConfigSpec.BooleanValue config = CONFIG_CLIENT.getPossibilityOf(action);
			setter = config::set;
			getter = config::get;
			serverWideLimitation = () -> info.getServerLimitation().isPermitted(action);
			individualLimitation = () -> info.getIndividualLimitation().isPermitted(action);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int type) {//type:1->right 0->left
		for (ModeSet modeSet : modeMenuList) {
			if (modeSet.isMouseIn((int) mouseX, (int) mouseY) && type == 0) {
				modeSet.set();
				return true;
			}
		}
		switch (mode) {
			case Actions:
				for (CheckboxButton button : actionButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
				break;
			case Configs:
				for (CheckboxButton button : configButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
		}
		return false;
	}
}
