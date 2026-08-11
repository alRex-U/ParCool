package com.alrex.parcool.common.action;

import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.MultiActionStateSetPacket;
import com.alrex.parcool.common.network.MultiComposablePacket;
import com.alrex.parcool.common.network.PacketHelper;

import java.util.LinkedList;

public class ActionSynchronizationDepot {
    private final LinkedList<ActionStateSetPacket> packets = new LinkedList<>();

    public void requestSync(ActionStateSetPacket packet) {
        packets.add(packet);
    }

    public void tick() {
        if (!packets.isEmpty()) {
            PacketHelper.sendToAll(
                    MultiActionStateSetPacket.ID,
                    packToPacket(),
                    MultiComposablePacket::encode
            );
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
