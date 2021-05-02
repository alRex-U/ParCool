package com.alrex.parcool.client.hud;


import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.IStamina;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaHUD extends AbstractGui {
	public static void render(RenderGameOverlayEvent event) {
		if (!ParCoolConfig.CONFIG_CLIENT.ParCoolActivation.get()) return;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		IStamina stamina;
		{
			LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			if (!staminaOptional.isPresent()) return;
			stamina = staminaOptional.resolve().get();
		}
		MainWindow window = Minecraft.getInstance().getMainWindow();
		final int width = window.getScaledWidth();
		final int height = window.getScaledHeight();
		final int boxWidth = 100;
		final int boxHeight = 20;
		final int heartWidth = boxHeight - 9;
		final int staminaWidth = boxWidth - heartWidth - 10;
		int x = width - boxWidth - 1;
		int y = height - boxHeight - 1;

		double staminaScale = (double) stamina.getStamina() / stamina.getMaxStamina();
		if (staminaScale < 0) staminaScale = 0;
		if (staminaScale > 1) staminaScale = 1;
		int color = getStaminaColor(staminaScale, stamina.isExhausted());

		AbstractGui.fill(event.getMatrixStack(), x, y, x + boxWidth, y + boxHeight, 0xAA585654);
		AbstractGui.fill(event.getMatrixStack(), x + 2, y + 2, x + boxWidth - 2, y + boxHeight - 2, 0xAA898989);
		AbstractGui.fill(event.getMatrixStack(), x + heartWidth + 7, y + 4, x + heartWidth + 6 + staminaWidth, y + 5 + heartWidth, 0xAA2B2B2B);
		AbstractGui.fill(event.getMatrixStack(), x + heartWidth + 7, y + 4, x + heartWidth + 6 + (int) Math.round(staminaWidth * staminaScale), y + 5 + heartWidth, color);
		renderYellowHeart(event.getMatrixStack(), x + 4, y + 4, heartWidth, heartWidth);
	}

	private static void renderYellowHeart(MatrixStack stack, int x, int y, int width, int height) {
		AbstractGui.blit(stack, x, y, width, height, 161f, 1f, 7, 7, 256, 256);
	}

	private static int getStaminaColor(double factor, boolean exhausted) {
		if (exhausted) return 0xCC993C3A;
		if (factor > 0.5) {
			return getColorCodeFromARGB(0xCC, (int) (0xFF * (1 - factor) * 2), 0xFF, 0);
		} else {
			return getColorCodeFromARGB(0xCC, 0xFF, (int) (0xFF * (factor * 2)), 0);
		}
	}

	private static int getColorCodeFromARGB(int a, int r, int g, int b) {
		return a * 0x1000000 + r * 0x10000 + g * 0x100 + b;
	}

	@SubscribeEvent
	public static void onOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
		render(event);
	}
}
