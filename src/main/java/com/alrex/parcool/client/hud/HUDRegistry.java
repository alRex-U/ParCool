package com.alrex.parcool.client.hud;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.hud.stamina.StaminaHUDController;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@OnlyIn(Dist.CLIENT)
public class HUDRegistry {
	private static HUDRegistry instance = null;

	private final StaminaHUDController staminaHUD = new StaminaHUDController();


	public static HUDRegistry getInstance() {
		if (instance == null) instance = new HUDRegistry();
		return instance;
	}

    public void onSetup(RegisterGuiLayersEvent event) {
        event.registerAbove(ResourceLocation.fromNamespaceAndPath("minecraft", "food_level"), ParCool.resourceLocation("hud.stamina.host"), staminaHUD);
	}

    public void onTick(ClientTickEvent.Post event) {
		staminaHUD.onTick(event);
	}
}
