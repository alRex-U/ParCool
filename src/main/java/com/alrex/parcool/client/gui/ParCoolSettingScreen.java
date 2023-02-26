package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.info.ActionInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import org.lwjgl.glfw.GLFW;

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
	private final Checkbox[] configButtons = new Checkbox[configItemList.length];
	private final ActionConfigSet[] actionList = new ActionConfigSet[ActionList.ACTIONS.size()];
	private final InfoSet[] infoList;
	private final Checkbox[] actionButtons = new Checkbox[actionList.length];
	private final ModeSet[] modeMenuList = new ModeSet[]{
			new ModeSet(new TranslatableComponent("parcool.gui.text.action"), SettingMode.Actions, 0, this),
			new ModeSet(new TranslatableComponent("parcool.gui.text.config"), SettingMode.Configs, 1, this),
			new ModeSet(new TranslatableComponent("parcool.gui.text.limitation"), SettingMode.Limitations, 2, this)
	};
	private int modeIndex;

	public ParCoolSettingScreen(BaseComponent titleIn, ActionInfo info, ColorTheme theme) {
		super(titleIn);
		for (int i = 0; i < actionList.length; i++) {
			actionList[i] = new ActionConfigSet(ActionList.getByIndex(i), info);
			actionButtons[i] = new Checkbox(0, 0, 0, Checkbox_Item_Height, new TextComponent(actionList[i].name), actionList[i].getter.getAsBoolean());
		}
		for (int i = 0; i < configItemList.length; i++) {
			configButtons[i] = new Checkbox(0, 0, 0, Checkbox_Item_Height, new TranslatableComponent(configItemList[i].name), configItemList[i].getter.getAsBoolean());
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
		serverPermissionReceived = info.getServerLimitation()::isReceived;
		individualPermissionReceived = info.getIndividualLimitation()::isReceived;
		color = theme;
	}

	private int topIndex = 0;
	private int viewableItemCount = 0;
	private static final int Checkbox_Item_Height = 21;
	private final ColorTheme color;
	private final BooleanSupplier serverPermissionReceived;
	private final BooleanSupplier individualPermissionReceived;

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

	private static final BaseComponent MenuTitle = new TranslatableComponent("parcool.gui.title.setting");

	@Override
	public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
		super.render(matrixStack, mouseX, mouseY, p_230430_4_);
		renderBackground(matrixStack, 0);
		int topBarHeight = font.lineHeight * 2;
		int topBarItemWidth = (int) (1.6 * Arrays.stream(modeMenuList).map(it -> font.width(it.title)).max(Integer::compareTo).orElse(0));
		int topBarOffsetX = width - topBarItemWidth * modeMenuList.length;
		fillGradient(matrixStack, 0, 0, this.width, topBarHeight, color.getTopBar1(), color.getTopBar2());
		drawString(
				matrixStack, font, MenuTitle,
				10,
				topBarHeight / 4 + 1,
				color.getText()
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
					selected ? color.getText() : color.getSubText()
			);
			fill(matrixStack, item.x, 2, item.x + 1, topBarHeight - 3, color.getSeparator());
		}
		fill(matrixStack, 0, topBarHeight - 1, width, topBarHeight, color.getSeparator());
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

	private static final BaseComponent Header_ActionName = new TranslatableComponent("parcool.gui.text.actionName");
	private static final BaseComponent Header_ServerPermission = new TextComponent("G");
	private static final BaseComponent Header_ServerPermissionText = new TranslatableComponent("parcool.gui.text.globalPermission");
	private static final BaseComponent Header_IndividualPermission = new TextComponent("I");
	private static final BaseComponent Header_IndividualPermissionText = new TranslatableComponent("parcool.gui.text.individualPermission");
	private static final BaseComponent Permission_Permitted = new TextComponent("✓");
	private static final BaseComponent Permission_Denied = new TextComponent("×");
	private static final BaseComponent Permission_Not_Received = new TextComponent("§4Error:Permissions are not sent from a server.\nPlease check whether ParCool is installed or re-login to the server.§r");

	private void renderActions(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40, headerHeight = (int) (font.lineHeight * 1.5f);
		int headerOffsetY = offsetY + font.lineHeight * 2;
		int contentOffsetY = headerOffsetY + headerHeight + 2;
		int permissionColumnWidth = font.width(Permission_Permitted) * 5;
		int nameColumnWidth = width - offsetX * 2 - permissionColumnWidth * 2;
		int contentHeight = height - contentOffsetY - font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		int headerTextY = headerOffsetY + headerHeight / 2 - font.lineHeight / 2 + 1;
		drawString(matrixStack, font, Header_ActionName, offsetX + 5, headerTextY, color.getText());
		drawCenteredString(matrixStack, font, Header_ServerPermission, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
		drawCenteredString(matrixStack, font, Header_IndividualPermission, offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
		for (Checkbox actionButton : actionButtons) {
			actionButton.setWidth(0);
		}
		for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
			Checkbox button = actionButtons[i + topIndex];
			button.x = offsetX + 1;
			button.y = contentOffsetY + Checkbox_Item_Height * i;
			button.setWidth(nameColumnWidth - 5);
			button.setHeight(20);
			button.render(matrixStack, mouseX, mouseY, p_230430_4_);
			fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, color.getSubSeparator());
			int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
			boolean permitted = actionList[topIndex + i].serverWideLimitation.getAsBoolean();
			drawCenteredString(
					matrixStack, font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
			permitted = actionList[topIndex + i].individualLimitation.getAsBoolean();
			drawCenteredString(
					matrixStack, font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
		}
		fillGradient(matrixStack, 0, offsetY, width, headerOffsetY, color.getHeader1(), color.getHeader2());
		fillGradient(matrixStack, 0, contentOffsetY + contentHeight, width, height, color.getHeader1(), color.getHeader2());
		drawCenteredString(matrixStack, font, modeMenuList[0].title, width / 2, offsetY + font.lineHeight / 2 + 2, color.getStrongText());
		if (topIndex + viewableItemCount < actionButtons.length)
			drawCenteredString(matrixStack, font, new TextComponent("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getStrongText());
		//draw separators
		fill(matrixStack, offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, color.getSeparator());
		fill(matrixStack, offsetX, headerOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		fill(matrixStack, offsetX + nameColumnWidth, headerOffsetY, offsetX + nameColumnWidth + 1, contentOffsetY + contentHeight, color.getSeparator());
		fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth + 1, contentOffsetY + contentHeight, color.getSeparator());
		fill(matrixStack, offsetX + nameColumnWidth + permissionColumnWidth * 2, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth * 2 + 1, contentOffsetY + contentHeight, color.getSeparator());
		{// draw tooltip
			int columnCenter = offsetX + nameColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				if (serverPermissionReceived.getAsBoolean())
					renderComponentTooltip(matrixStack, Collections.singletonList(Header_ServerPermissionText), mouseX, mouseY);
				else
					renderComponentTooltip(
							matrixStack,
							Arrays.asList(Header_ServerPermissionText, Permission_Not_Received),
							mouseX, mouseY);
			}

			columnCenter = offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				if (individualPermissionReceived.getAsBoolean())
					renderComponentTooltip(matrixStack, Collections.singletonList(Header_IndividualPermissionText), mouseX, mouseY);
				else
					renderComponentTooltip(
							matrixStack,
							Arrays.asList(Header_IndividualPermissionText, Permission_Not_Received),
							mouseX, mouseY);
			}
		}
	}

	private void renderConfigs(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40;
		int contentWidth = width - offsetX * 2;
		int contentHeight = height - offsetY - font.lineHeight * 4;
		int contentOffsetY = offsetY + font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		for (Checkbox configButton : configButtons) {
			configButton.setWidth(0);
		}
		for (int i = 0; i < viewableItemCount && i + topIndex < configItemList.length; i++) {
			Checkbox button = configButtons[i + topIndex];
			button.x = offsetX + 1;
			button.y = offsetY + font.lineHeight * 2 + Checkbox_Item_Height * i;
			button.setWidth(contentWidth);
			button.setHeight(20);
			button.render(matrixStack, mouseX, mouseY, p_230430_4_);
			fill(matrixStack, offsetX, button.y + button.getHeight(), width - offsetX, button.y + button.getHeight() + 1, color.getSubSeparator());
		}
		fill(matrixStack, width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, color.getSeparator());
		fill(matrixStack, offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		fillGradient(matrixStack, 0, offsetY, width, offsetY + font.lineHeight * 2, color.getHeader1(), color.getHeader2());
		fillGradient(matrixStack, 0, offsetY + contentHeight + font.lineHeight * 2, width, height, color.getHeader1(), color.getHeader2());
		drawCenteredString(matrixStack, font, modeMenuList[1].title, width / 2, offsetY + font.lineHeight / 2 + 2, color.getStrongText());
		if (topIndex + viewableItemCount < configButtons.length)
			drawCenteredString(matrixStack, font, new TextComponent("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getSubText());
	}

	private void renderLimitations(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
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
					color.getText()
			);
			drawString(
					matrixStack, font,
					item.value,
					width - offsetX - 5 - valueWidth,
					contentOffsetY + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
					color.getText()
			);
			fill(matrixStack, offsetX, contentOffsetY + itemHeight * (i + 1), width - offsetX, contentOffsetY + itemHeight * (i + 1) + 1, color.getSubSeparator());
		}
		fill(matrixStack, width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, color.getSeparator());
		fill(matrixStack, offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		fillGradient(matrixStack, 0, offsetY, width, offsetY + font.lineHeight * 2, color.getHeader1(), color.getHeader2());
		fillGradient(matrixStack, 0, offsetY + contentHeight + font.lineHeight * 2, width, height, color.getHeader1(), color.getHeader2());
		drawCenteredString(matrixStack, font, modeMenuList[2].title, width / 2, offsetY + font.lineHeight / 2 + 2, 0x8888FF);
	}

	@Override
	public void renderBackground(@Nonnull PoseStack p_238651_1_, int p_238651_2_) {
		fill(p_238651_1_, 0, 0, this.width, this.height, color.getBackground());
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, p_238651_1_));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case GLFW.GLFW_KEY_RIGHT:
				if (modeIndex < modeMenuList.length - 1) {
					modeIndex++;
					modeMenuList[modeIndex].set();
				}
				break;
			case GLFW.GLFW_KEY_LEFT:
				if (modeIndex > 0) {
					modeIndex--;
					modeMenuList[modeIndex].set();
				}
				break;
			case GLFW.GLFW_KEY_UP:
				mouseScrolled(0, 0, 1);
				break;
			case GLFW.GLFW_KEY_DOWN:
				mouseScrolled(0, 0, -1);
				break;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
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
		final BaseComponent title;
		final SettingMode mode;
		final ParCoolSettingScreen parent;
		final int index;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		ModeSet(BaseComponent title, SettingMode mode, int index, ParCoolSettingScreen parent) {
			this.title = title;
			this.index = index;
			this.mode = mode;
			this.parent = parent;
		}

		void set() {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			parent.topIndex = 0;
			parent.mode = mode;
			parent.modeIndex = index;
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
			name = new TranslatableComponent("parcool.action." + action.getSimpleName()).getString();
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
				for (Checkbox button : actionButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
				break;
			case Configs:
				for (Checkbox button : configButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
		}
		return false;
	}
}
