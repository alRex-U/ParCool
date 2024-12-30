package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventConsumerForEpicFight {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onHUDRender(ParCoolHUDEvent.RenderEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (EpicFightManager.isUsingEpicFightStamina(player)) {
            event.setCanceled(true);
        }
    }
}
