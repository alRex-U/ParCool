package com.alrex.parcool.common.network;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaBroadcastPayload;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class StaminaSynchronizationBroadcaster {
    private static Map<UUID, ReadonlyStamina> staminaMap = new TreeMap<>();

    /// This have to be called from same thread as game ticking in logical server
    public static void add(UUID playerID, ReadonlyStamina stamina) {
        staminaMap.put(playerID, stamina);
    }

    private static void send() {
        if (staminaMap.isEmpty()) return;
        var payloads = staminaMap.entrySet().stream().map(entry -> new StaminaPayload(entry.getKey(), entry.getValue())).toList();
        staminaMap = new TreeMap<>();
        PacketDistributor.sendToAllPlayers(new StaminaBroadcastPayload(payloads));
    }

    private static int syncCoolTime = 10;

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        if (--syncCoolTime > 0) return;
        syncCoolTime = 10;
        send();
    }
}
