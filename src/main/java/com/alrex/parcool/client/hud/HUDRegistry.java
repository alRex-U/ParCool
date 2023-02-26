package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HUDRegistry {
	private static HUDRegistry instance = null;

	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	@SubscribeEvent
	public void onSetup(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("hud.stamina.host", StaminaHUDController.getInstance());
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		StaminaHUDController.getInstance().onTick(event);
	}
}
