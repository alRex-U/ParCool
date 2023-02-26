package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class HUDRegistry {
	private static HUDRegistry instance = null;
	private static IIngameOverlay Stamina_HUD = null;

	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	@SubscribeEvent
	public void onSetup(FMLCommonSetupEvent event) {
		Stamina_HUD = OverlayRegistry.registerOverlayTop("ParCool Stamina", StaminaHUDController.getInstance());
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		StaminaHUDController.getInstance().onTick(event);
	}
}
