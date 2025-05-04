package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.HideInBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class PlayerVisibilityHandler {
    @SubscribeEvent
    public static void onLivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            if (parkourability.get(HideInBlock.class).isDoing()) {
                event.modifyVisibility(event.getVisibilityModifier() * 0.1);
            }
        }

    }
}
