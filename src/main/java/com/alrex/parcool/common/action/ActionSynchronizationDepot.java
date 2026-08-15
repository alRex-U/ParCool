package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.MultiActionStateSetPacket;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.LinkedList;

public class ActionSynchronizationDepot {
    private final LinkedList<ActionStateSetPacket> packets = new LinkedList<>();

    public void requestSync(ActionStateSetPacket packet) {
        packets.add(packet);
    }

    public void tick() {
        if (!packets.isEmpty()) {
            PacketDistributor.sendToAllPlayers(packToPacket());
        }
    }

    private MultiActionStateSetPacket packToPacket() {
        var multiPacket = new MultiActionStateSetPacket();
        for (var packet : packets) {
            multiPacket.add(packet);
        }
        packets.clear();
        return multiPacket;
    }
}
