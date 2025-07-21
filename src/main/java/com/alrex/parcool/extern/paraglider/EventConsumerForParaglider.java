package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.extern.AdditionalMods;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

public class EventConsumerForParaglider {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onUpdateAnimateInfo(ParCoolAnimationInfoEvent event) {
        if (AdditionalMods.paraglider().isFallingWithParaglider(event.getPlayer())) {
            event.getOption().cancelAnimation();
        }
    }
}