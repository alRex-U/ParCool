package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


public abstract class ParCoolSettingScreen extends Screen {
	private final ScreenSet<?>[] screenList;
	protected int currentScreen = 0;

	public ParCoolSettingScreen(ITextComponent titleIn, ActionInfo info, ColorTheme theme) {
		super(titleIn);
		serverPermissionReceived = info.getServerLimitation()::isSynced;
		color = theme;
		screenList = new ScreenSet[]{
				new ScreenSet<>(new TranslationTextComponent("parcool.gui.text.action"), () -> new SettingActionLimitationScreen(title, info, theme)),
				new ScreenSet<>(new StringTextComponent(I18n.get("parcool.gui.text.config") + "1"), () -> new SettingBooleanConfigScreen(titleIn, info, theme)),
				new ScreenSet<>(new StringTextComponent(I18n.get("parcool.gui.text.config") + "2"), () -> new SettingEnumConfigScreen(titleIn, info, theme)),
				new ScreenSet<>(new TranslationTextComponent("parcool.gui.text.limitation"), () -> new SettingShowLimitationsScreen(titleIn, info, theme)),
		};
	}

	protected int topIndex = 0;
	protected int viewableItemCount = 0;
	protected static final int Checkbox_Item_Height = 21;
	protected final ColorTheme color;
	protected final BooleanSupplier serverPermissionReceived;

	@Override
	public void resize(@Nonnull Minecraft minecraft, int p_231152_2_, int p_231152_3_) {
		super.resize(minecraft, p_231152_2_, p_231152_3_);
		mouseScrolled(0, 0, 0);
	}

	private static final ITextComponent MenuTitle = new TranslationTextComponent("parcool.gui.title.setting");

	@Override
	public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
		super.render(matrixStack, mouseX, mouseY, p_230430_4_);
		renderBackground(matrixStack, 0);
		int topBarHeight = font.lineHeight * 2;
		int topBarItemWidth = (int) (1.2 * Arrays.stream(screenList).map(it -> font.width(it.title)).max(Integer::compareTo).orElse(0));
		int topBarOffsetX = width - topBarItemWidth * screenList.length;
		fillGradient(matrixStack, 0, 0, this.width, topBarHeight, color.getTopBar1(), color.getTopBar2());
		renderSubHeaderAndFooter(matrixStack, screenList[currentScreen].title, isDownScrollable(), topBarHeight);
		renderContents(matrixStack, mouseX, mouseY, p_230430_4_, topBarHeight + font.lineHeight * 2, font.lineHeight * 2);
		for (int i = 0; i < screenList.length; i++) {
			ScreenSet<?> item = screenList[i];
			item.y = 0;
			item.x = topBarOffsetX + i * topBarItemWidth;
			item.width = topBarItemWidth;
			item.height = topBarHeight;
			boolean selected = currentScreen == i || item.isMouseIn(mouseX, mouseY);
			drawCenteredString(
					matrixStack, font, item.title,
					topBarOffsetX + i * topBarItemWidth + topBarItemWidth / 2,
					topBarHeight / 4 + 1,
					selected ? color.getText() : color.getSubText()
			);
			fill(matrixStack, item.x, 2, item.x + 1, topBarHeight - 3, color.getSeparator());
		}
		fill(matrixStack, 0, topBarHeight - 1, width, topBarHeight, color.getSeparator());

		int titleOffset = 0;
		if (!serverPermissionReceived.getAsBoolean()) {
			fill(matrixStack, 2, 2, topBarHeight - 3, topBarHeight - 3, 0xFFEEEEEE);
			fill(matrixStack, 3, 3, topBarHeight - 4, topBarHeight - 4, 0xFFEE0000);
			drawCenteredString(matrixStack, font, "!", topBarHeight / 2, (topBarHeight - font.lineHeight) / 2 + 1, 0xEEEEEE);
			if (2 <= mouseX && mouseX < topBarHeight - 3 && 1 <= mouseY && mouseY < topBarHeight - 3) {
				renderComponentTooltip(
						matrixStack,
						Collections.singletonList(Permission_Not_Received),
						mouseX, mouseY);
			}
			titleOffset = topBarHeight;
		}
		drawString(
				matrixStack, font, MenuTitle,
				titleOffset + 5,
				topBarHeight / 4 + 1,
				color.getText()
		);
	}

	protected static final ITextComponent Header_ActionName = new TranslationTextComponent("parcool.gui.text.actionName");
	protected static final ITextComponent Header_Limitation = new StringTextComponent("L");
	protected static final ITextComponent Header_Limitation_Text = new TranslationTextComponent("parcool.gui.text.limitation");
	protected static final ITextComponent Permission_Permitted = new StringTextComponent("✓");
	protected static final ITextComponent Permission_Denied = new StringTextComponent("×");
	protected static final ITextComponent Permission_Not_Received = new StringTextComponent("§4[Error] Permissions are not sent from a server.\n\nBy closing this setting menu, permissions will be sent again.\nIf it were not done, please report to the mod developer after checking whether ParCool is installed and re-login to the server.§r");

	protected abstract void renderContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset);

	protected void save() {
	}

	protected boolean isDownScrollable() {
		return false;
	}

	private void renderSubHeaderAndFooter(MatrixStack matrixStack, ITextComponent title, boolean scrollable, int topOffset) {
		int headerHeight = font.lineHeight * 2;
		fillGradient(matrixStack, 0, topOffset, width, topOffset + headerHeight, color.getHeader1(), color.getHeader2());
		fillGradient(matrixStack, 0, height - headerHeight, width, height, color.getHeader1(), color.getHeader2());
		drawCenteredString(matrixStack, font, title, width / 2, topOffset + font.lineHeight / 2 + 2, color.getStrongText());
		if (scrollable)
			drawCenteredString(matrixStack, font, new StringTextComponent("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getStrongText());
	}

	@Override
	public void renderBackground(@Nonnull MatrixStack p_238651_1_, int p_238651_2_) {
		fill(p_238651_1_, 0, 0, this.width, this.height, color.getBackground());
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, p_238651_1_));
	}

	@Override
	public void onClose() {
		save();
		super.onClose();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case GLFW.GLFW_KEY_RIGHT:
				save();
				Minecraft.getInstance().setScreen(screenList[(currentScreen + 1) % screenList.length].screenSupplier.get());
				break;
			case GLFW.GLFW_KEY_LEFT:
				save();
				Minecraft.getInstance().setScreen(screenList[(currentScreen - 1) % screenList.length].screenSupplier.get());
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
		int scroll = (int) -Math.signum(value);
		if (scroll <= 0 || isDownScrollable()) {
			topIndex += scroll;
		}
		if (topIndex < 0) topIndex = 0;
		return true;
	}


	private static class ScreenSet<T extends ParCoolSettingScreen> {
		final Supplier<T> screenSupplier;
		final ITextComponent title;
		int x;
		int y;
		int width;
		int height;

		boolean isMouseIn(double mouseX, double mouseY) {
			return x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height;
		}

		public ScreenSet(ITextComponent title, Supplier<T> supplier) {
			this.title = title;
			this.screenSupplier = supplier;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int type) {//type:1->right 0->left
		for (ScreenSet<?> modeSet : screenList) {
			if (modeSet.isMouseIn((int) mouseX, (int) mouseY) && type == 0) {
				save();
				Minecraft.getInstance().setScreen(modeSet.screenSupplier.get());
				return true;
			}
		}
		return false;
	}
}
