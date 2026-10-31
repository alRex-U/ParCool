package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.MultiPlayerActionPacket;
import com.alrex.parcool.common.network.PlayerActionPacket;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedList;
import java.util.UUID;

public class ActionSynchronizationDepot {
    private record Entry(UUID playerID, ActionStateSetPacket stateSetPacket, boolean acceptsInLocalPlayer) {
    }

    private final LinkedList<Entry> entries = new LinkedList<>();

    public void requestSync(UUID playerID, ActionStateSetPacket packet, boolean acceptsInLocalPlayer) {
        entries.add(new Entry(playerID, packet, acceptsInLocalPlayer));
    }

    public void tick() {
        if (!entries.isEmpty()) {
            ParCool.getConnection().send(PacketDistributor.ALL.noArg(), packToPacket());
        }
    }

    private MultiPlayerActionPacket packToPacket() {
        var multiPacket = new MultiPlayerActionPacket();
        for (var entry : entries) {
            var packet = new PlayerActionPacket.StoC();
            packet.set(entry.playerID, new PlayerActionPacket(entry.stateSetPacket), entry.acceptsInLocalPlayer);
            multiPacket.add(packet);
        }
        entries.clear();
        return multiPacket;
    }
}
