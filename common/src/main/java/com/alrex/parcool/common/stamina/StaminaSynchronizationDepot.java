package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.network.MultiStaminaPacket;
import com.alrex.parcool.common.network.PacketHelper;
import com.alrex.parcool.common.network.StaminaPacket;

import java.util.TreeMap;
import java.util.UUID;

// for logical server
public class StaminaSynchronizationDepot {
    private final TreeMap<UUID, ReadonlyStamina> map = new TreeMap<>();
    private int syncCooldown = 20;

    public void requestSync(UUID playerID, ReadonlyStamina stamina) {
        map.put(playerID, stamina);
    }

    public void tick() {
        if (syncCooldown > 0) {
            syncCooldown--;
        } else {
            if (!map.isEmpty()) {
                PacketHelper.sendToAll(
                        MultiStaminaPacket.ID,
                        packToPacket(),
                        MultiStaminaPacket::encode
                );
            }
            syncCooldown = 20;
        }
    }

    private MultiStaminaPacket packToPacket() {
        var packet = new MultiStaminaPacket();
        for (var entry : map.entrySet()) {
            packet.add(new StaminaPacket(entry.getKey(), false, entry.getValue()));
        }
        map.clear();
        return packet;
    }
}
