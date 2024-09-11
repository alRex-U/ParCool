package com.alrex.parcool.client.hud;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class HUDRegistry {
    @SubscribeEvent
    public static void onRegisterGui(RegisterGuiLayersEvent event) {
        HUDManager.getInstance().registerHUD(event);
    }
}
