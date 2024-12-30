package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.HungerStamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
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
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		AbstractClientPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get() ||
				(IStamina.get(player) instanceof HungerStamina)
		)
			return;

		if (MinecraftForge.EVENT_BUS.post(new ParCoolHUDEvent.RenderEvent(gui, graphics, partialTick, width, height)))
			return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(gui, graphics, partialTick, width, height);
				break;
			case Normal:
				staminaHUD.render(gui, graphics, partialTick, width, height);
				break;
		}
	}
}
