package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerJoinHandler {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide()) return;
        Entity entity = event.getEntity();
        PlayerWrapper player = PlayerWrapper.getOrDefault(entity);
        if (player == null || !ClientPlayerWrapper.is(player)) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        SyncClientInformationMessage.sync(ClientPlayerWrapper.get(player), true);
    }
}
