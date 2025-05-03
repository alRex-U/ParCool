package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerVisibilityHandler {
    @SubscribeEvent
    public static void onLivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        Entity entity = event.getEntity();
        PlayerWrapper player = PlayerWrapper.getOrDefault(entity);
        if (player == null) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (parkourability.get(HideInBlock.class).isDoing()) {
            event.modifyVisibility(event.getVisibilityModifier() * 0.1);
        }

    }
}
