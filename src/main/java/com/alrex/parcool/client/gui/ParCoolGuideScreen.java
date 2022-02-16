package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.guidebook.Book;
import com.alrex.parcool.client.gui.guidebook.BookDecoder;
import com.alrex.parcool.client.gui.widget.ListView;
import com.alrex.parcool.utilities.FontUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alrex.parcool.utilities.ColorUtil.getColorCodeFromARGB;

@OnlyIn(Dist.CLIENT)
public class ParCoolGuideScreen extends Screen {
	public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("parcool:textures/gui/book_background.png");

	private static final int PAGE_HOME = -1;
	private int cachedPageNumber = 0;
	private List<ITextProperties> cachedPage = null;
	private int currentPage = PAGE_HOME;
	private int scrollValue = 0;
	private final Book book = BookDecoder.getInstance().getBook();
	private final List<String> menuList = book.getPages()
			.stream()
			.map((Book.Page::getTitle))
			.map(ITextProperties::getString)
			.collect(Collectors.toList());
	private final int width = 300;
	private int height = (int) (width * 0.75);
	private final int menuOffsetX = 20;
	private final int menuOffsetY = 30;
	private int bookOffsetX = 0;
	private int bookOffsetY = 0;
	private final ListView menu = new ListView(menuList);

	protected ParCoolGuideScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	public ParCoolGuideScreen() {
		super(new StringTextComponent("ParCool"));
	}

	//init?
	@Override
	public void func_231023_e_() {
		menu.setListener(this::changePage);
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
		bookOffsetX = (window.getScaledWidth() - width) / 2;
		bookOffsetY = (window.getScaledHeight() - height) / 2;

		AbstractGui.func_238466_a_(stack, bookOffsetX, bookOffsetY, width, height, 0f, 0f, 256, 194, 256, 256);
		renderContent(stack, bookOffsetX, bookOffsetY, width / 2, height, mouseX, mouseY, n);
		renderMenu(stack, bookOffsetX + width / 2, bookOffsetY, width / 2, height, mouseX, mouseY, n);
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

	//mouseScrolled?
	@Override
	public boolean func_231043_a_(double x, double y, double value) {
		super.func_231043_a_(x, y, value);
		scroll((int) -value);
		return true;
	}

	//keyPressed?
	@Override
	public boolean func_231046_a_(int type, int p_231046_2_, int p_231046_3_) {
		if (super.func_231046_a_(type, p_231046_2_, p_231046_3_)) return true;
		switch (type) {
			case GLFW.GLFW_KEY_UP:
				scroll(-1);
				return true;
			case GLFW.GLFW_KEY_DOWN:
				scroll(1);
				return true;
		}
		return false;
	}

	//mouseClicked?
	@Override
	public boolean func_231044_a_(double mouseX, double mouseY, int type) {//type:1->right 0->left
		menu.onClick(type, mouseX, mouseY);
		return false;
	}

	private void renderContent(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		if (currentPage == PAGE_HOME) {
			renderHome(stack, left, top, width, height, mouseX, mouseY, n);
		} else {
			renderContentText(stack, left, top, width, height, mouseX, mouseY, n);
		}
	}

	private void scroll(int value) {
		if (cachedPage == null) return;

		scrollValue += value;
		if (scrollValue < 0) scrollValue = 0;
		if (cachedPage.size() < scrollValue) scrollValue = cachedPage.size();
	}

	private void renderHome(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		Minecraft mc = this.getMinecraft();
		FontRenderer fontRenderer = this.field_230712_o_;
		final int offsetY = 20;
		final int center = left + width / 2;
		ITextProperties textTitle = ITextProperties.func_240653_a_("ParCool!", Style.field_240709_b_.func_240713_a_(true));
		ITextProperties textSubtitle = ITextProperties.func_240652_a_("Guide Book");
		AbstractGui.func_238466_a_(stack, left + width / 4, top + offsetY + 50, width / 2, width / 2, 0f, 207f, 52, 49, 256, 256);
		FontUtil.drawCenteredText(stack, textTitle, center, top + offsetY + 10, getColorCodeFromARGB(0xFF, 0x55, 0x55, 0xFF));
		FontUtil.drawCenteredText(stack, textSubtitle, center, top + offsetY + 15 + fontRenderer.FONT_HEIGHT, getColorCodeFromARGB(0xFF, 0x44, 0x44, 0xBB));
	}

	private void renderContentText(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		Minecraft mc = this.getMinecraft();
		FontRenderer fontRenderer = this.field_230712_o_;

		final int offsetY = 15;
		final int offsetX = 14;
		final int lineHeight = fontRenderer.FONT_HEIGHT + 1;
		final int titleHeight = fontRenderer.FONT_HEIGHT + 10;
		final int contentHeight = height - offsetX * 2 - titleHeight;
		final int contentLine = contentHeight / lineHeight;

		if (currentPage < 0 || book.getPages().size() <= currentPage) return;

		Book.Page page = book.getPages().get(currentPage);
		if (cachedPageNumber != currentPage || cachedPage == null) {
			ArrayList<ITextProperties> wrappedLines = new ArrayList<>();
			page.getContent().forEach((ITextProperties text) -> {
				if (text.getString().isEmpty()) {
					wrappedLines.add(StringTextComponent.field_240750_d_);
				} else
					wrappedLines.addAll(fontRenderer.func_238420_b_().func_238362_b_(text, (int) (width - offsetX * 1.6), Style.field_240709_b_));
			});
			cachedPage = wrappedLines;
			cachedPageNumber = currentPage;
		}

		FontUtil.drawCenteredText(stack, page.getTitle(), left + width / 2, top + offsetY + titleHeight / 2, getColorCodeFromARGB(255, 0, 0, 0));

		IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		for (int i = 0; i + scrollValue < Math.min(contentLine + scrollValue, cachedPage.size()); i++) {
			fontRenderer.renderString(
					cachedPage.get(i + scrollValue).getString(),
					left + offsetX,
					top + offsetY + titleHeight + i * (lineHeight),
					getColorCodeFromARGB(0xFF, 0, 0, 0),
					false,
					stack.getLast().getMatrix(),
					renderTypeBuffer,
					true,
					0,
					15728880
			);
		}
		renderTypeBuffer.finish();
	}

	private void renderMenu(MatrixStack stack, int left, int top, int width, int height, int mouseX, int mouseY, float n) {
		FontRenderer fontRenderer = this.field_230712_o_;
		int offsetY = 20;
		FontUtil.drawCenteredText(stack, ITextProperties.func_240652_a_("Index"), left + width / 2, top + offsetY, getColorCodeFromARGB(0xFF, 0x66, 0x66, 0xFF));

		menu.setX(left + menuOffsetX);
		menu.setY(top + menuOffsetY);
		menu.setWidth(width - menuOffsetX * 2);
		menu.setHeight(height - menuOffsetY * 2);
		menu.render(stack, fontRenderer, mouseX, mouseY, n);
	}

	private void changePage(int i) {
		if (i != PAGE_HOME && (i < 0 || book.getPages().size() <= i)) return;
		scrollValue = 0;
		currentPage = i;
		PlayerEntity player = Minecraft.getInstance().player;
		if (player != null) player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
	}

}