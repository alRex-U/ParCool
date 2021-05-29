package com.alrex.parcool.common.item.gui;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ParCoolGuideScreen extends Screen {
	public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("parcool:textures/gui/book_background.png");

	private static final int PAGE_HOME = -1;
	private static final int PAGE_SETTINGS = -2;
	private static int currentPage = PAGE_HOME;
	private List<ITextProperties> pages = getPages();
	private final List<Button> menuButtons = Arrays.asList(
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("About This Mod"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Stamina"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("CatLeap"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Crawl"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Dodge"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("FastRunning"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("GrabCliff"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Roll"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Vault"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("WallJump"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Sliding"), this::onPress),
			new Button(0, 0, 0, 0, ITextComponent.func_241827_a_("Settings"), this::openSetting)
	);
	private final Color color = Color.func_240743_a_(getColorCodeFromARGB(0xFF, 0x99, 0x99, 0xBB));
	private final List<CheckboxButton> settingButtons = Arrays.asList(
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("CatLeap").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canCatLeap.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("Crawl").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canCrawl.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("Dodge").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canDodge.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("FastRunning").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canFastRunning.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("FrontFlip").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canFrontFlip.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("GrabCliff").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canGrabCliff.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("Roll").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canRoll.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("Vault").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canVault.get()),
			new CheckboxButton(0, 0, 0, 0, new StringTextComponent("WallJump").func_230530_a_(Style.field_240709_b_.func_240718_a_(color)), ParCoolConfig.CONFIG_CLIENT.canWallJump.get())
	);

	private void syncSettings() {
		List<CheckboxButton> b = settingButtons;
		assert b.size() == 9;
		ParCoolConfig.CONFIG_CLIENT.canCatLeap.set(b.get(0).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canCrawl.set(b.get(1).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canDodge.set(b.get(2).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canFastRunning.set(b.get(3).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canFrontFlip.set(b.get(4).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canGrabCliff.set(b.get(5).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canRoll.set(b.get(6).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canVault.set(b.get(7).isChecked());
		ParCoolConfig.CONFIG_CLIENT.canWallJump.set(b.get(8).isChecked());
	}

	protected ParCoolGuideScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	public ParCoolGuideScreen() {
		super(ITextComponent.func_241827_a_("ParCool"));
	}

	//init?
	@Override
	public void func_231023_e_() {
		super.func_231023_e_();
	}

	//render?
	@Override
	public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float n) {
		func_230446_a_(stack);
		Minecraft mc = this.getMinecraft();
		mc.getTextureManager().bindTexture(BACKGROUND_LOCATION);
		func_238651_a_(stack, getColorCodeFromARGB(0x77, 0x66, 0x66, 0xCC));
		MainWindow window = mc.getMainWindow();
		int width = 250;
		int height = (int) (width * 0.75);
		int offsetX = (window.getScaledWidth() - width) / 2;
		int offsetY = (window.getScaledHeight() - height) / 2;

		AbstractGui.func_238466_a_(stack, offsetX, offsetY, width, height, 0f, 0f, 256, 192, 256, 256);
		renderContent(stack, offsetX, offsetY, width / 2, height, mouseX, mouseY, n);
		renderMenu(stack, offsetX + width / 2, offsetY, width / 2, height, mouseX, mouseY, n);
	}

	//renderBackground?
	@Override
	public void func_230446_a_(MatrixStack p_230446_1_) {
		super.func_230446_a_(p_230446_1_);
	}

	//renderBackground?
	@Override
	public void func_238651_a_(MatrixStack p_238651_1_, int p_238651_2_) {
		super.func_238651_a_(p_238651_1_, p_238651_2_);
	}

	//keyPressed?
	@Override
	public boolean func_231046_a_(int type, int p_231046_2_, int p_231046_3_) {
		syncSettings();
		if (super.func_231046_a_(type, p_231046_2_, p_231046_3_)) return true;
		switch (type) {
			case GLFW.GLFW_KEY_UP:
				changePage(currentPage - 1);
				return true;
			case GLFW.GLFW_KEY_DOWN:
				changePage(currentPage + 1);
				return true;
		}
		return false;
	}

	//mouseClicked?
	@Override
	public boolean func_231044_a_(double mouseX, double mouseY, int type) {//type:1->right 0->left
		if (type == 0) {
			if (currentPage == PAGE_SETTINGS) {
				settingButtons.stream().filter(button -> {
					int x = button.field_230690_l_;
					int y = button.field_230691_m_;
					int height = button.getHeight();
					int width = button.func_230998_h_();
					return (x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height);
				}).findFirst().ifPresent(CheckboxButton::func_230930_b_);
			}
			menuButtons.stream().filter(button -> {
				int x = button.field_230690_l_;
				int y = button.field_230691_m_;
				int height = button.getHeight();
				int width = button.func_230998_h_();
				return (x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height);
			}).findFirst().ifPresent(Button::func_230930_b_);
			return true;
		}
		return false;
	}

	private void renderContent(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		switch (currentPage) {
			case PAGE_HOME:
				renderHome(stack, left, top, width, height, mouseX, mouseY, n);
				break;
			case PAGE_SETTINGS:
				renderSettings(stack, left, top, width, height, mouseX, mouseY, n);
				break;
			default:
				renderContentText(stack, left, top, width, height, mouseX, mouseY, n);
				break;
		}
	}

	private void renderHome(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		Minecraft mc = this.getMinecraft();
		FontRenderer fontRenderer = this.field_230712_o_;
		final int offsetY = 20;
		final int center = left + width / 2;
		ITextProperties textTitle = ITextProperties.func_240653_a_("ParCool!", Style.field_240709_b_.func_240713_a_(true));
		ITextProperties textSubtitle = ITextProperties.func_240652_a_("Guide Book");
		drawCenteredText(stack, textTitle, center, top + offsetY + 10, getColorCodeFromARGB(0xFF, 0x55, 0x55, 0xFF));
		drawCenteredText(stack, textSubtitle, center, top + offsetY + 15 + fontRenderer.FONT_HEIGHT, getColorCodeFromARGB(0xFF, 0x44, 0x44, 0xBB));
	}

	private void renderContentText(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		Minecraft mc = this.getMinecraft();
		FontRenderer fontRenderer = this.field_230712_o_;
		final int offsetY = 20;
		final int offsetX = 10;
		if (currentPage < 0 || pages.size() <= currentPage) return;
		ITextProperties text = pages.get(currentPage);
		List<ITextProperties> wrappedLine = fontRenderer.func_238425_b_(text, width - offsetX * 2);
		for (int i = 0; i < wrappedLine.size(); i++) {
			fontRenderer.func_238422_b_(stack, wrappedLine.get(i), left + offsetX, top + offsetY + i * (fontRenderer.FONT_HEIGHT) + 3, getColorCodeFromARGB(0xFF, 0, 0, 0));
		}
	}

	private void renderMenu(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		FontRenderer fontRenderer = this.field_230712_o_;
		int offsetY = 20;
		int offsetX = 20;
		int buttonWidth = width - offsetX * 2;
		int y = (int) (top + offsetY * 1.5);
		drawCenteredText(stack, ITextProperties.func_240652_a_("Index"), left + width / 2, top + offsetY, getColorCodeFromARGB(0xFF, 0x66, 0x66, 0xFF));
		for (Button button : menuButtons) {
			button.func_230991_b_(buttonWidth);//width
			button.setHeight(fontRenderer.FONT_HEIGHT + 2);
			button.field_230690_l_ = left + offsetX;//x
			button.field_230691_m_ = y;//y
			button.func_230431_b_(stack, mouseX, mouseY, n);
			y += button.getHeight();
		}
	}

	private void renderSettings(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		FontRenderer fontRenderer = this.field_230712_o_;
		int offsetY = 20;
		int offsetX = 20;
		int buttonWidth = width - offsetX * 2;
		int y = (int) (top + offsetY * 1.5);
		drawCenteredText(stack, ITextProperties.func_240652_a_("Enabled Actions"), left + width / 2, top + offsetY, getColorCodeFromARGB(0xFF, 0x66, 0x66, 0xFF));
		for (CheckboxButton button : settingButtons) {
			button.func_230991_b_(buttonWidth);
			button.setHeight(fontRenderer.FONT_HEIGHT + 6);
			button.field_230690_l_ = left + offsetX;
			button.field_230691_m_ = y;
			button.func_230431_b_(stack, mouseX, mouseY, n);
			y += button.getHeight();
		}
	}

	private void drawCenteredText(MatrixStack stack, ITextProperties text, int x, int y, int color) {
		FontRenderer fontRenderer = this.field_230712_o_;
		int width = fontRenderer.getStringWidth(text.getString());
		fontRenderer.func_238422_b_(stack, text, x - (width >> 1), y - (fontRenderer.FONT_HEIGHT >> 1), color);
	}

	private static int getColorCodeFromARGB(int a, int r, int g, int b) {
		return a * 0x1000000 + r * 0x10000 + g * 0x100 + b;
	}

	private static List<ITextProperties> getPages() {
		final String path = "/assets/parcool/book/parcool_guide_content.txt";
		BufferedReader reader = new BufferedReader(new InputStreamReader(ParCool.class.getResourceAsStream(path), StandardCharsets.UTF_8));
		ArrayList<String> texts = new ArrayList<>();
		//=======
		// replace division line -> \\n and set to list
		Iterator<String> iterator = reader.lines().iterator();
		Pattern division = Pattern.compile("===+");
		AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());
		iterator.forEachRemaining((line -> {
			if (!division.matcher(line).matches()) {
				builder.get().append(line).append("\n");
			} else {//division line
				texts.add(builder.toString());
				if (iterator.hasNext()) builder.set(new StringBuilder());
			}
		}));
		//=======
		return texts.stream().map(ITextProperties::func_240652_a_).collect(Collectors.toList());
	}

	private void changePage(int i) {
		if (i != PAGE_HOME && i != PAGE_SETTINGS && (i < 0 || pages.size() <= i)) return;
		currentPage = i;
	}

	private void onPress(Button button) {
		changePage(menuButtons.indexOf(button));
	}

	private void openSetting(Button button) {
		changePage(PAGE_SETTINGS);
	}
}
