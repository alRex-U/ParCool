package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class HUDRegistry {
	private static HUDRegistry instance = null;

	private final StaminaHUDController staminaHUD = new StaminaHUDController();

	private static IIngameOverlay Stamina_HUD = null;

	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	@SubscribeEvent
	public void onSetup(FMLClientSetupEvent event) {
		Stamina_HUD = OverlayRegistry.registerOverlayTop("ParCool Stamina", staminaHUD);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		staminaHUD.onTick(event);
	}
}
