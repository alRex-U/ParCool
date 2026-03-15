package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.ActionStateBroadcastPayload;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ActionSynchronizationBroadcaster {
    private static List<ActionStatePayload> pendingPayloads = new ArrayList<>();

    /// This have to be called from same thread as game ticking in logical server
    public static void add(ActionStatePayload payload) {
        pendingPayloads.addLast(payload);
    }

    private static void send() {
        if (pendingPayloads.isEmpty()) return;
        PacketDistributor.sendToAllPlayers(new ActionStateBroadcastPayload(pendingPayloads));
        pendingPayloads = new ArrayList<>();
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        send();
    }
}
