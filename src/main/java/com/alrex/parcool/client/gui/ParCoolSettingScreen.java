package com.alrex.parcool.client.gui;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;


public class ParCoolSettingScreen extends Screen {

	private final ParCoolConfig.Client.Booleans[] booleans = ParCoolConfig.Client.Booleans.values();

	private enum SettingMode {Actions, Configs, Limitations}

	private enum ConfigMode {Boolean, Enum}

	private SettingMode mode = SettingMode.Actions;
	private ConfigMode configMode = ConfigMode.Boolean;
	private final Checkbox[] configButtons = new Checkbox[booleans.length];
	private final ActionConfigSet[] actionList = new ActionConfigSet[ActionList.ACTIONS.size()];
	private final InfoSet[] infoList;
	private final Checkbox[] actionButtons = new Checkbox[actionList.length];
	private final ModeSet[] modeMenuList = new ModeSet[]{
			new ModeSet(Component.translatable("parcool.gui.text.action"), SettingMode.Actions, 0, this),
			new ModeSet(Component.translatable("parcool.gui.text.config"), SettingMode.Configs, 1, this),
			new ModeSet(Component.translatable("parcool.gui.text.limitation"), SettingMode.Limitations, 2, this)
	};
	private final EnumConfigSet<?>[] enumConfigList = new EnumConfigSet[]{
			new EnumConfigSet<>(ParCoolConfig.Client.AlignHorizontalStaminaHUD),
			new EnumConfigSet<>(ParCoolConfig.Client.AlignVerticalStaminaHUD),
			new EnumConfigSet<>(ParCoolConfig.Client.FastRunControl),
			new EnumConfigSet<>(ParCoolConfig.Client.CrawlControl),
			new EnumConfigSet<>(ParCoolConfig.Client.VaultAnimationMode),
			new EnumConfigSet<>(ParCoolConfig.Client.GUIColorTheme),
			new EnumConfigSet<>(ParCoolConfig.Client.StaminaHUDType),
	};
	private final Button[] enumConfigButtons = new Button[enumConfigList.length];
	private int modeIndex;

	public ParCoolSettingScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
		super(titleIn);
		for (int i = 0; i < actionList.length; i++) {
			actionList[i] = new ActionConfigSet(ActionList.getByIndex(i), info);
			actionButtons[i] = new Checkbox(0, 0, 0, Checkbox_Item_Height, Component.literal(actionList[i].name), actionList[i].getter.getAsBoolean());
		}
		for (int i = 0; i < booleans.length; i++) {
			configButtons[i] = new Checkbox(0, 0, 0, Checkbox_Item_Height, Component.translatable(booleans[i].Path), booleans[i].get());
		}
		for (int i = 0; i < enumConfigList.length; i++) {
			int index = i;
			enumConfigButtons[index] = new Button.Builder(Component.literal(enumConfigList[index].get().toString()), it -> {
				enumConfigList[index].next();
				it.setMessage(Component.literal(enumConfigList[index].get().toString()));
			}).build();
		}
		infoList = new InfoSet[]{
				new InfoSet(
						"Max Stamina",
						Integer.toString(info.getMaxStamina())
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
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			ParCool.LOGGER.error("ParCool in-game setting menu could not save and sync the values. Something wrong");
			super.onClose();
			return;
		}
		for (int i = 0; i < booleans.length; i++) {
			booleans[i].set(configButtons[i].selected());
		}
		for (int i = 0; i < actionList.length; i++) {
			actionList[i].setter.accept(actionButtons[i].selected());
		}
		SyncClientInformationMessage.sync(player, true);
		super.onClose();
	}

	@Override
	public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
		super.resize(p_231152_1_, p_231152_2_, p_231152_3_);
		mouseScrolled(0, 0, 0);
	}
	private static final Component MenuTitle = Component.translatable("parcool.gui.title.setting");

	@Override
	public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_) {
		super.render(graphics, mouseX, mouseY, p_230430_4_);
		renderBackground(graphics, mouseX, mouseY, p_230430_4_);
		int topBarHeight = font.lineHeight * 2;
		int topBarItemWidth = (int) (1.6 * Arrays.stream(modeMenuList).map(it -> font.width(it.title)).max(Integer::compareTo).orElse(0));
		int topBarOffsetX = width - topBarItemWidth * modeMenuList.length;
		graphics.fillGradient(0, 0, this.width, topBarHeight, color.getTopBar1(), color.getTopBar2());
		for (int i = 0; i < modeMenuList.length; i++) {
			ModeSet item = modeMenuList[i];
			item.y = 0;
			item.x = topBarOffsetX + i * topBarItemWidth;
			item.width = topBarItemWidth;
			item.height = topBarHeight;
			boolean selected = (item.mode == mode) || item.isMouseIn(mouseX, mouseY);
			graphics.drawCenteredString(
					font, item.title,
					topBarOffsetX + i * topBarItemWidth + topBarItemWidth / 2,
					topBarHeight / 4 + 1,
					selected ? color.getText() : color.getSubText()
			);
			graphics.fill(item.x, 2, item.x + 1, topBarHeight - 3, color.getSeparator());
		}
		graphics.fill(0, topBarHeight - 1, width, topBarHeight, color.getSeparator());
		switch (mode) {
			case Actions:
				renderActions(graphics, mouseX, mouseY, p_230430_4_, topBarHeight);
				break;
			case Configs:
				renderConfigs(graphics, mouseX, mouseY, p_230430_4_, topBarHeight);
				break;
			case Limitations:
				renderLimitations(graphics, mouseX, mouseY, p_230430_4_, topBarHeight);
		}
		int titleOffset = 0;
		if (!(serverPermissionReceived.getAsBoolean() && individualPermissionReceived.getAsBoolean())) {
			graphics.fill(2, 2, topBarHeight - 3, topBarHeight - 3, 0xFFEEEEEE);
			graphics.fill(3, 3, topBarHeight - 4, topBarHeight - 4, 0xFFEE0000);
			graphics.drawCenteredString(font, "!", topBarHeight / 2, (topBarHeight - font.lineHeight) / 2 + 1, 0xEEEEEE);
			if (2 <= mouseX && mouseX < topBarHeight - 3 && 1 <= mouseY && mouseY < topBarHeight - 3) {
				graphics.renderComponentTooltip(
						font,
						Collections.singletonList(Permission_Not_Received),
						mouseX, mouseY);
			}
			titleOffset = topBarHeight;
		}
		graphics.drawString(
				font, MenuTitle,
				titleOffset + 5,
				topBarHeight / 4 + 1,
				color.getText()
		);
	}

	private static final Component Header_ActionName = Component.translatable("parcool.gui.text.actionName");
	private static final Component Header_ServerPermission = Component.literal("G");
	private static final Component Header_ServerPermissionText = Component.translatable("parcool.gui.text.globalPermission");
	private static final Component Header_IndividualPermission = Component.literal("I");
	private static final Component Header_IndividualPermissionText = Component.translatable("parcool.gui.text.individualPermission");
	private static final Component Permission_Permitted = Component.literal("✓");
	private static final Component Permission_Denied = Component.literal("×");
	private static final Component Permission_Not_Received = Component.literal("§4[Error] Permissions are not sent from a server.\n\nBy closing this setting menu, permissions will be sent again.\nIf it were not done, please report to the mod developer after checking whether ParCool is installed and re-login to the server.§r");

	private void renderActions(GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40, headerHeight = (int) (font.lineHeight * 1.5f);
		int headerOffsetY = offsetY + font.lineHeight * 2;
		int contentOffsetY = headerOffsetY + headerHeight + 2;
		int permissionColumnWidth = font.width(Permission_Permitted) * 5;
		int nameColumnWidth = width - offsetX * 2 - permissionColumnWidth * 2;
		int contentHeight = height - contentOffsetY - font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		int headerTextY = headerOffsetY + headerHeight / 2 - font.lineHeight / 2 + 1;
		graphics.drawString(font, Header_ActionName, offsetX + 5, headerTextY, color.getText());
		graphics.drawCenteredString(font, Header_ServerPermission, offsetX + nameColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
		graphics.drawCenteredString(font, Header_IndividualPermission, offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2, headerTextY, color.getText());
		for (Checkbox actionButton : actionButtons) {
			actionButton.setWidth(0);
		}
		for (int i = 0; i < viewableItemCount && i + topIndex < actionButtons.length; i++) {
			Checkbox button = actionButtons[i + topIndex];
			button.setX(offsetX + 1);
			button.setY(contentOffsetY + Checkbox_Item_Height * i);
			button.setWidth(nameColumnWidth - 5);
			button.setHeight(20);
			button.render(graphics, mouseX, mouseY, p_230430_4_);
			graphics.fill(offsetX, button.getY() + button.getHeight(), width - offsetX, button.getY() + button.getHeight() + 1, color.getSubSeparator());
			int rowY = contentOffsetY + Checkbox_Item_Height * i + Checkbox_Item_Height / 2;
			boolean permitted = actionList[topIndex + i].serverWideLimitation.getAsBoolean();
			graphics.drawCenteredString(
					font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
			permitted = actionList[topIndex + i].individualLimitation.getAsBoolean();
			graphics.drawCenteredString(
					font,
					permitted ? Permission_Permitted : Permission_Denied,
					offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2,
					rowY - font.lineHeight / 2,
					permitted ? 0x00AA00 : 0xAA0000
			);
		}
		graphics.fillGradient(0, offsetY, width, headerOffsetY, color.getHeader1(), color.getHeader2());
		graphics.fillGradient(0, contentOffsetY + contentHeight, width, height, color.getHeader1(), color.getHeader2());
		graphics.drawCenteredString(font, modeMenuList[0].title, width / 2, offsetY + font.lineHeight / 2 + 2, color.getStrongText());
		if (topIndex + viewableItemCount < actionButtons.length)
			graphics.drawCenteredString(font, Component.literal("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getStrongText());
		//draw separators
		graphics.fill(offsetX, contentOffsetY, width - offsetX, contentOffsetY - 1, color.getSeparator());
		graphics.fill(offsetX, headerOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fill(offsetX + nameColumnWidth, headerOffsetY, offsetX + nameColumnWidth + 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fill(offsetX + nameColumnWidth + permissionColumnWidth, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth + 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fill(offsetX + nameColumnWidth + permissionColumnWidth * 2, headerOffsetY, offsetX + nameColumnWidth + permissionColumnWidth * 2 + 1, contentOffsetY + contentHeight, color.getSeparator());
		{// draw tooltip
			int columnCenter = offsetX + nameColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				graphics.renderComponentTooltip(font, Collections.singletonList(Header_ServerPermissionText), mouseX, mouseY);
			}

			columnCenter = offsetX + nameColumnWidth + permissionColumnWidth + permissionColumnWidth / 2;
			if ((headerOffsetY < mouseY && mouseY < headerOffsetY + headerHeight)
					&& (columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2)
			) {
				graphics.renderComponentTooltip(font, Collections.singletonList(Header_IndividualPermissionText), mouseX, mouseY);
			}
		}
	}

	private void renderConfigs(GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40;
		int contentWidth = width - offsetX * 2;
		int contentHeight = height - offsetY - font.lineHeight * 4;
		int contentOffsetY = offsetY + font.lineHeight * 2;
		viewableItemCount = contentHeight / Checkbox_Item_Height;
		for (Checkbox configButton : configButtons) {
			configButton.setWidth(0);
		}
		for (Button configButton : enumConfigButtons) {
			configButton.setWidth(0);
		}
		switch (configMode) {
			case Boolean: {
				for (int i = 0; i < viewableItemCount && i + topIndex < booleans.length; i++) {
					Checkbox button = configButtons[i + topIndex];
					button.setX(offsetX + 1);
					button.setY(offsetY + font.lineHeight * 2 + Checkbox_Item_Height * i);
					button.setWidth(contentWidth);
					button.setHeight(20);
					button.render(graphics, mouseX, mouseY, p_230430_4_);
					graphics.fill(offsetX, button.getY() + button.getHeight(), width - offsetX, button.getY() + button.getHeight() + 1, color.getSubSeparator());
					String comment = booleans[i + topIndex].Comment;
					if (comment != null && button.getX() < mouseX && mouseX < button.getX() + contentWidth && button.getY() < mouseY && mouseY < button.getY() + 20) {
						graphics.renderComponentTooltip(
								font,
								Collections.singletonList(Component.literal(comment)),
								mouseX, mouseY);
					}
				}
				break;
			}
			case Enum: {
				for (int i = 0; i < viewableItemCount && i + topIndex < enumConfigList.length; i++) {
					Button button = enumConfigButtons[i + topIndex];
					int buttonWidth = contentWidth / 3;
					button.setX(width - offsetX - buttonWidth - 1);
					button.setY(offsetY + font.lineHeight * 2 + Checkbox_Item_Height * i);
					button.setWidth(buttonWidth);
					button.setHeight(20);
					button.render(graphics, mouseX, mouseY, p_230430_4_);
					List<String> path = enumConfigList[i + topIndex].configInstance.getPath();
					graphics.drawString(font, path.get(path.size() - 1), offsetX + 6, button.getY() + 1 + (button.getHeight() - font.lineHeight) / 2, color.getText());
					graphics.fill(offsetX, button.getY() + button.getHeight(), width - offsetX, button.getY() + button.getHeight() + 1, color.getSubSeparator());
				}
				break;
			}
		}
		graphics.fill(width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fill(offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fillGradient(0, offsetY, width, offsetY + font.lineHeight * 2, color.getHeader1(), color.getHeader2());
		graphics.fillGradient(0, offsetY + contentHeight + font.lineHeight * 2, width, height, color.getHeader1(), color.getHeader2());
		graphics.drawCenteredString(font, modeMenuList[1].title, width / 2, offsetY + font.lineHeight / 2 + 2, color.getStrongText());
		if (mouseX < offsetX) {
			graphics.fill(0, contentOffsetY, offsetX, contentOffsetY + contentHeight, ColorUtil.multiple(color.getBackground(), 1.8));
		} else if (width - offsetX < mouseX) {
			graphics.fill(width - offsetX + 1, contentOffsetY, width, contentOffsetY + contentHeight, ColorUtil.multiple(color.getBackground(), 1.8));
		}
		graphics.drawCenteredString(font, Component.literal("▶"), width - offsetX / 2, contentOffsetY + contentHeight / 2, color.getText());
		graphics.drawCenteredString(font, Component.literal("◀"), offsetX / 2, contentOffsetY + contentHeight / 2, color.getText());
		if (topIndex + viewableItemCount < configButtons.length)
			graphics.drawCenteredString(font, Component.literal("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getSubText());
	}

	private void renderLimitations(GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_, int offsetY) {
		int offsetX = 40;
		int contentHeight = height - offsetY - font.lineHeight * 4;
		int contentOffsetY = offsetY + font.lineHeight * 2;
		int itemHeight = font.lineHeight * 2;
		int valueWidth = Arrays.stream(infoList).map(it -> font.width(it.value)).max(Integer::compareTo).orElse(0);
		for (int i = 0; i < infoList.length; i++) {
			InfoSet item = infoList[i];
			graphics.drawString(
					font,
					item.name,
					offsetX + 5,
					contentOffsetY + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
					color.getText()
			);
			graphics.drawString(
					font,
					item.value,
					width - offsetX - 5 - valueWidth,
					contentOffsetY + itemHeight * i + itemHeight / 2 - font.lineHeight / 2,
					color.getText()
			);
			graphics.fill(offsetX, contentOffsetY + itemHeight * (i + 1), width - offsetX, contentOffsetY + itemHeight * (i + 1) + 1, color.getSubSeparator());
		}
		graphics.fill(width - offsetX, contentOffsetY, width - offsetX - 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fill(offsetX, contentOffsetY, offsetX + 1, contentOffsetY + contentHeight, color.getSeparator());
		graphics.fillGradient(0, offsetY, width, offsetY + font.lineHeight * 2, color.getHeader1(), color.getHeader2());
		graphics.fillGradient(0, offsetY + contentHeight + font.lineHeight * 2, width, height, color.getHeader1(), color.getHeader2());
		graphics.drawCenteredString(font, modeMenuList[2].title, width / 2, offsetY + font.lineHeight / 2 + 2, 0x8888FF);
	}

	@Override
	public void renderBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(0, 0, this.width, this.height, color.getBackground());
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
	public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_299502_) {
		return mouseScrolled(p_94686_, p_94687_, p_94688_);
	}

	public boolean mouseScrolled(double x, double y, double value) {
		topIndex -= (int) Math.signum(value);
		switch (mode) {
			case Actions:
				if (topIndex > actionButtons.length - viewableItemCount)
					topIndex = actionButtons.length - viewableItemCount;
				break;
			case Configs:
				if (configMode == ConfigMode.Boolean) {
					if (topIndex > configButtons.length - viewableItemCount)
						topIndex = configButtons.length - viewableItemCount;
				} else if (configMode == ConfigMode.Enum) {
					if (topIndex > enumConfigButtons.length - viewableItemCount)
						topIndex = enumConfigButtons.length - viewableItemCount;
				}
		}
		if (topIndex < 0) topIndex = 0;
		return true;
	}

	private static class EnumConfigSet<T extends Enum<T>> {
		final ForgeConfigSpec.EnumValue<T> configInstance;
		final T[] values;

		public EnumConfigSet(ForgeConfigSpec.EnumValue<T> configInstance) {
			this.configInstance = configInstance;
			values = configInstance.get().getDeclaringClass().getEnumConstants();
		}

		public void next() {
			int index = (configInstance.get().ordinal() + 1) % values.length;
			configInstance.set(values[index]);
		}

		public T get() {
			return configInstance.get();
		}
	}

	private static class ModeSet {
		final Component title;
		final SettingMode mode;
		final ParCoolSettingScreen parent;
		final int index;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		ModeSet(Component title, SettingMode mode, int index, ParCoolSettingScreen parent) {
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
			name = Component.translatable("parcool.action." + action.getSimpleName()).getString();
			ForgeConfigSpec.BooleanValue config = ParCoolConfig.Client.getPossibilityOf(action);
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
				int offsetX = 40;
				if (mouseX < offsetX) {
					configMode = ConfigMode.values()[Math.floorMod(configMode.ordinal() - 1, ConfigMode.values().length)];
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
					break;
				} else if (width - offsetX < mouseX) {
					configMode = ConfigMode.values()[(configMode.ordinal() + 1) % ConfigMode.values().length];
					topIndex = 0;
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
					break;
				}
				for (Checkbox button : configButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
				for (Button button : enumConfigButtons) {
					button.mouseClicked(mouseX, mouseY, type);
				}
		}
		return false;
	}
}
