package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.stamina.StaminaHUDController;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class HUDRegistry {
	private static HUDRegistry instance = null;

	private final StaminaHUDController staminaHUD = new StaminaHUDController();


	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

	@SubscribeEvent
	public void onSetup(RegisterGuiOverlaysEvent event) {
		event.registerAbove(new ResourceLocation("minecraft", "food_level"), "hud.stamina.host", staminaHUD);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		staminaHUD.onTick(event);
	}
}
