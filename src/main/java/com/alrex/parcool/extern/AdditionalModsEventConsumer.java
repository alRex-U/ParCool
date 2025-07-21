package com.alrex.parcool.extern;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

public class AdditionalModsEventConsumer {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onHUDRender(ParCoolHUDEvent.RenderEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (AdditionalMods.isUsingExternalStamina()) {
            event.setCanceled(true);
        }
    }
}
