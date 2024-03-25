package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


public abstract class ParCoolSettingScreen extends Screen {
    private final ScreenSet<?>[] screenList;
    protected int currentScreen = 0;

	public ParCoolSettingScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
		super(titleIn);
		serverPermissionReceived = info.getServerLimitation()::isReceived;
		individualPermissionReceived = info.getIndividualLimitation()::isReceived;
		color = theme;
        screenList = new ScreenSet[]{
                new ScreenSet<>(Component.translatable("parcool.gui.text.action"), () -> new SettingActionLimitationScreen(title, info, theme)),
                new ScreenSet<>(Component.literal(I18n.get("parcool.gui.text.config") + "1"), () -> new SettingBooleanConfigScreen(titleIn, info, theme)),
                new ScreenSet<>(Component.literal(I18n.get("parcool.gui.text.config") + "2"), () -> new SettingEnumConfigScreen(titleIn, info, theme)),
                new ScreenSet<>(Component.translatable("parcool.gui.text.limitation"), () -> new SettingShowLimitationsScreen(titleIn, info, theme)),
        };
	}

    protected int topIndex = 0;
    protected int viewableItemCount = 0;
    protected static final int Checkbox_Item_Height = 21;
    protected final ColorTheme color;
    protected final BooleanSupplier serverPermissionReceived;
    protected final BooleanSupplier individualPermissionReceived;

	@Override
    public void resize(@Nonnull Minecraft minecraft, int p_231152_2_, int p_231152_3_) {
        super.resize(minecraft, p_231152_2_, p_231152_3_);
		mouseScrolled(0, 0, 0);
	}
	private static final Component MenuTitle = Component.translatable("parcool.gui.title.setting");

	@Override
	public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_) {
		super.render(graphics, mouseX, mouseY, p_230430_4_);
		renderBackground(graphics, mouseX, mouseY, p_230430_4_);
		int topBarHeight = font.lineHeight * 2;
        int topBarItemWidth = (int) (1.2 * Arrays.stream(screenList).map(it -> font.width(it.title)).max(Integer::compareTo).orElse(0));
        int topBarOffsetX = width - topBarItemWidth * screenList.length;
		graphics.fillGradient(0, 0, this.width, topBarHeight, color.getTopBar1(), color.getTopBar2());
        renderSubHeaderAndFooter(graphics, screenList[currentScreen].title, isDownScrollable(), topBarHeight);
        renderContents(graphics, mouseX, mouseY, p_230430_4_, topBarHeight + font.lineHeight * 2, font.lineHeight * 2);
        for (int i = 0; i < screenList.length; i++) {
            ScreenSet<?> item = screenList[i];
			item.y = 0;
			item.x = topBarOffsetX + i * topBarItemWidth;
			item.width = topBarItemWidth;
			item.height = topBarHeight;
            boolean selected = currentScreen == i || item.isMouseIn(mouseX, mouseY);
			graphics.drawCenteredString(
					font, item.title,
					topBarOffsetX + i * topBarItemWidth + topBarItemWidth / 2,
					topBarHeight / 4 + 1,
					selected ? color.getText() : color.getSubText()
			);
			graphics.fill(item.x, 2, item.x + 1, topBarHeight - 3, color.getSeparator());
		}
		graphics.fill(0, topBarHeight - 1, width, topBarHeight, color.getSeparator());

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

    protected static final Component Header_ActionName = Component.translatable("parcool.gui.text.actionName");
    protected static final Component Header_ServerPermission = Component.literal("G");
    protected static final Component Header_ServerPermissionText = Component.translatable("parcool.gui.text.globalPermission");
    protected static final Component Header_IndividualPermission = Component.literal("I");
    protected static final Component Header_IndividualPermissionText = Component.translatable("parcool.gui.text.individualPermission");
    protected static final Component Permission_Permitted = Component.literal("✓");
    protected static final Component Permission_Denied = Component.literal("×");
    protected static final Component Permission_Not_Received = Component.literal("§4[Error] Permissions are not sent from a server.\n\nBy closing this setting menu, permissions will be sent again.\nIf it were not done, please report to the mod developer after checking whether ParCool is installed and re-login to the server.§r");

    protected abstract void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset);

    protected void save() {
    }

    protected boolean isDownScrollable() {
        return false;
    }

    private void renderSubHeaderAndFooter(GuiGraphics graphics, Component title, boolean scrollable, int topOffset) {
        int headerHeight = font.lineHeight * 2;
        graphics.fillGradient(0, topOffset, width, topOffset + headerHeight, color.getHeader1(), color.getHeader2());
        graphics.fillGradient(0, height - headerHeight, width, height, color.getHeader1(), color.getHeader2());
        graphics.drawCenteredString(font, title, width / 2, topOffset + font.lineHeight / 2 + 2, color.getStrongText());
        if (scrollable)
			graphics.drawCenteredString(font, Component.literal("↓"), width / 2, height - font.lineHeight - font.lineHeight / 2, color.getStrongText());
	}

	@Override
	public void renderBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(0, 0, this.width, this.height, color.getBackground());
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
	public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_299502_) {
		return mouseScrolled(p_94686_, p_94687_, p_94688_);
	}

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
		final Component title;
        int x;
        int y;
        int width;
        int height;

        boolean isMouseIn(double mouseX, double mouseY) {
            return x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height;
        }

        public ScreenSet(Component title, Supplier<T> supplier) {
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
