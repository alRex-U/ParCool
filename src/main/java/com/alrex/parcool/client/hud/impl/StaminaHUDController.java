package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.paraglider.ParagliderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController implements IGuiOverlay {
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
		staminaHUD = new StaminaHUD();
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;
		lightStaminaHUD.onTick(event, player);
		staminaHUD.onTick(event, player);
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get() ||
				ParCoolConfig.Client.Booleans.UseHungerBarInstead.get() ||
				ParagliderManager.isUsingParaglider()
		)
			return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(gui, guiGraphics, partialTick, screenWidth, screenHeight);
				break;
			case Normal:
				staminaHUD.render(gui, guiGraphics, partialTick, screenWidth, screenHeight);
				break;
		}

	}
}
