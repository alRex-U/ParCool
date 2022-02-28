package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.ColorUtil;
import com.alrex.parcool.utilities.FontUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class RollDefermentHUD extends AbstractHUD {
	public RollDefermentHUD() {
		super(Position.DEFAULT);
	}

	@Override
	public void render(RenderGameOverlayEvent.Pre event, MatrixStack stack) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		Roll roll = parkourability.getRoll();
		if (!roll.isReady()) return;

		float scale = (roll.getReadyTick() + (1 - event.getPartialTicks())) / (float) Roll.ROLL_DEFERMENT_TICK;

		MainWindow window = event.getWindow();
		int width = window.getScaledWidth();
		int halfWidth = width / 2;
		int boxWidth = (int) (halfWidth * scale);
		int color = ColorUtil.getColorCodeFromARGB(128, 20, 120, 60);
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		func_238467_a_(stack, halfWidth, 0, halfWidth + boxWidth, fontRenderer.FONT_HEIGHT, color);
		func_238467_a_(stack, halfWidth - boxWidth, 0, halfWidth, fontRenderer.FONT_HEIGHT, color);
		FontUtil.drawCenteredText(stack, new StringTextComponent("<Roll Ready>"), halfWidth, Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2 + 1, ColorUtil.getColorCodeFromARGB(0, 240, 240, 240));
	}
}
