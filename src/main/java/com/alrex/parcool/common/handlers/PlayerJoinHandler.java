package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerJoinHandler {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (player instanceof LocalPlayer) {
                Parkourability parkourability = Parkourability.get(player);
                if (parkourability == null) return;
                parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
                SyncClientInformationMessage.sync((LocalPlayer) player, true);
            }
        }
    }
}
