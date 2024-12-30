package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventConsumerForParaglider {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onHUDRender(ParCoolHUDEvent.RenderEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ParagliderManager.isUsingParagliderStamina(player)) {
            event.setCanceled(true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onUpdateAnimateInfo(ParCoolAnimationInfoEvent event) {
        if (ParagliderManager.isFallingWithParaglider(event.getPlayer())) {
            event.getOption().cancelAnimation();
        }
    }
}
