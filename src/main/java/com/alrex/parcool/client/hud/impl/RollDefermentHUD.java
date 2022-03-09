package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.client.hud.AbstractHUD;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.ColorUtil;
import com.alrex.parcool.utilities.FontUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;


@OnlyIn(Dist.CLIENT)
public class RollDefermentHUD extends AbstractHUD {
	public RollDefermentHUD() {
		super(Position.DEFAULT);
	}

	@Override
	public void render(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		Roll roll = parkourability.getRoll();
		if (!roll.isReady()) return;

		float scale = (roll.getReadyTick() + (1 - partialTicks)) / (float) Roll.ROLL_DEFERMENT_TICK;

		int halfWidth = width / 2;
		int boxWidth = (int) (halfWidth * scale);
		int color = ColorUtil.getColorCodeFromARGB(128, 20, 120, 60);
		Font fontRenderer = Minecraft.getInstance().font;
		GuiComponent.fill(stack, halfWidth, 0, halfWidth + boxWidth, fontRenderer.lineHeight, color);
		GuiComponent.fill(stack, halfWidth - boxWidth, 0, halfWidth, fontRenderer.lineHeight, color);
		FontUtil.drawCenteredText(stack, "<Roll Ready>", halfWidth, Minecraft.getInstance().font.lineHeight / 2 + 1, ColorUtil.getColorCodeFromARGB(0, 240, 240, 240));
	}
}
