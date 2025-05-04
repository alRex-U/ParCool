package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventConsumerForFeathers {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onHUDRender(ParCoolHUDEvent.RenderEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (AdditionalMods.feathers().isUsingFeathers(player)) {
            event.setCanceled(true);
        }
    }
}
