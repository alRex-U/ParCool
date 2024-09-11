package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@OnlyIn(Dist.CLIENT)
public class HUDManager {
    private static HUDManager instance = null;

    private final StaminaHUDController staminaHUD = new StaminaHUDController();

    public static HUDManager getInstance() {
        if (instance == null) instance = new HUDManager();
        return instance;
    }

    public void onSetup() {
    }

    public void registerHUD(RegisterGuiLayersEvent event) {
        event.registerAboveAll(StaminaHUDController.ID, staminaHUD);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Post event) {
        staminaHUD.onTick(event);
    }
}
