package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEntitySizeHandler {
    @SubscribeEvent
    public static final void onEntitySize(EntityEvent.Size sizeEvent) {
        Entity entity = sizeEvent.getEntity();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            if (parkourability.get(HideInBlock.class).isDoing()) {
                sizeEvent.setNewEyeHeight(player.getEyeHeight(Pose.STANDING));
            }
        }
    }
}
