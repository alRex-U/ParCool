package com.alrex.parcool.common.handlers;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.ConfigSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ConfigSyncHandler {
    // HIGHEST so the config arrives before the other ParCool data sent on login
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ParCool.getConnection().send(PacketDistributor.PLAYER.with(() -> player), ConfigSyncPacket.create());
    }

    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (!ParCool.getConfig().isServerModConfig(event.getConfig())) return;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        server.execute(() -> ParCool.getConnection().send(PacketDistributor.ALL.noArg(), ConfigSyncPacket.create()));
    }
}
