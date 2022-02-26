package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.widget.WidgetListView;
import com.alrex.parcool.utilities.ColorUtil;
import com.alrex.parcool.utilities.FontUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.alrex.parcool.ParCoolConfig.CONFIG_CLIENT;

public class ParCoolSettingScreen extends Screen {
	private final int width = 400;
	private final int height = 225;
	private final int xOffset = 10;
	private final int yOffset = 10;
	private final ButtonSet[] itemList = new ButtonSet[]{
			new ButtonSet("action.name.catleap", CONFIG_CLIENT.canCatLeap::set, CONFIG_CLIENT.canCatLeap::get),
			new ButtonSet("action.name.crawl", CONFIG_CLIENT.canCrawl::set, CONFIG_CLIENT.canCrawl::get),
			new ButtonSet("action.name.frontflip", CONFIG_CLIENT.canFrontFlip::set, CONFIG_CLIENT.canFrontFlip::get),
			new ButtonSet("action.name.dodge", CONFIG_CLIENT.canDodge::set, CONFIG_CLIENT.canDodge::get),
			new ButtonSet("action.name.fastrun", CONFIG_CLIENT.canFastRunning::set, CONFIG_CLIENT.canFastRunning::get),
			new ButtonSet("action.name.clingtocliff", CONFIG_CLIENT.canClingToCliff::set, CONFIG_CLIENT.canClingToCliff::get),
			new ButtonSet("action.name.roll", CONFIG_CLIENT.canRoll::set, CONFIG_CLIENT.canRoll::get),
			new ButtonSet("action.name.vault", CONFIG_CLIENT.canVault::set, CONFIG_CLIENT.canVault::get),
			new ButtonSet("action.name.walljump", CONFIG_CLIENT.canWallJump::set, CONFIG_CLIENT.canWallJump::get),
			new ButtonSet("hide stamina HUD", CONFIG_CLIENT.hideStaminaHUD::set, CONFIG_CLIENT.hideStaminaHUD::get),
			new ButtonSet("use light Stamina HUD", CONFIG_CLIENT.useLightHUD::set, CONFIG_CLIENT.useLightHUD::get),
			new ButtonSet("auto-turning when WallJump", CONFIG_CLIENT.autoTurningWallJump::set, CONFIG_CLIENT.autoTurningWallJump::get),
			new ButtonSet("disable WallJump toward walls", CONFIG_CLIENT.disableWallJumpTowardWall::set, CONFIG_CLIENT.disableWallJumpTowardWall::get),
			new ButtonSet("disable a camera rotation of Rolling", CONFIG_CLIENT.disableCameraRolling::set, CONFIG_CLIENT.disableCameraRolling::get),
			new ButtonSet("disable a camera rotation of Dodge", CONFIG_CLIENT.disableCameraDodge::set, CONFIG_CLIENT.disableCameraDodge::get),
			new ButtonSet("ParCool is active", CONFIG_CLIENT.parCoolActivation::set, CONFIG_CLIENT.parCoolActivation::get)
	};
	private final WidgetListView<CheckboxButton> buttons = new WidgetListView<CheckboxButton>(
			0, 0, 0, 0,
			Arrays.stream(itemList)
					.map((ButtonSet item) ->
							new CheckboxButton
									(
											0, 0, 0, 0,
											new TranslationTextComponent(item.name),
											item.getter.getAsBoolean()
									))
					.collect(Collectors.toList()),
			Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 11
	);

	public ParCoolSettingScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	//render?
	@Override
	public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

		MainWindow window = Minecraft.getInstance().getMainWindow();
		buttons.setX((window.getScaledWidth() - width) + xOffset);
		buttons.setY((window.getScaledHeight() - height) + yOffset);
		buttons.setWidth(width - (xOffset * 2));
		buttons.setHeight(height - (yOffset * 2));

		func_238651_a_(p_230430_1_, ColorUtil.getColorCodeFromARGB(0x77, 0x66, 0x66, 0xCC));
		buttons.render(p_230430_1_, Minecraft.getInstance().fontRenderer, p_230430_2_, p_230430_3_, p_230430_4_);
		FontUtil.drawCenteredText(p_230430_1_, new StringTextComponent("ParCool Settings"), window.getScaledWidth() / 2, yOffset, 0x8888FF);
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
		if (buttons.contains(x, y)) {
			buttons.scroll((int) -value);
		}
		return true;
	}

	private static class ButtonSet {
		final String name;
		final Consumer<Boolean> setter;
		final BooleanSupplier getter;

		ButtonSet(String name, Consumer<Boolean> setter, BooleanSupplier getter) {
			this.name = name;
			this.getter = getter;
			this.setter = setter;
		}
	}

	//mouseClicked?
	@Override
	public boolean func_231044_a_(double mouseX, double mouseY, int type) {//type:1->right 0->left
		if (buttons.contains(mouseX, mouseY)) {
			Tuple<Integer, CheckboxButton> item = buttons.clicked(mouseX, mouseY, type);
			if (item == null) return false;
			if (item.getA() < 0 || itemList.length <= item.getA()) return false;
			ButtonSet selected = itemList[item.getA()];

			//reverse value?
			item.getB().func_230930_b_();
			selected.setter.accept(item.getB().isChecked());

			PlayerEntity player = Minecraft.getInstance().player;
			if (player != null) player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f);
		}
		return false;
	}
}
