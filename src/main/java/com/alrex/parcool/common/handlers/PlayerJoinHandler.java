package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerJoinHandler {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player instanceof ClientPlayerEntity) {
                Parkourability parkourability = Parkourability.get(player);
                if (parkourability == null) return;
                parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
                SyncClientInformationMessage.sync((ClientPlayerEntity) player, true);
            }
        }
    }
}
