package com.alrex.parcool.common.stamina;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.MultiStaminaPacket;
import com.alrex.parcool.common.network.StaminaPacket;
import net.minecraftforge.network.PacketDistributor;

import java.util.TreeMap;
import java.util.UUID;

// for logical server
public class StaminaSynchronizationDepot {
    private record Entry(ReadonlyStamina stamina, boolean acceptsInLocalPlayer) {
    }

    private final TreeMap<UUID, Entry> map = new TreeMap<>();
    private int syncCooldown = 20;

    public void requestSync(UUID playerID, ReadonlyStamina stamina, boolean acceptsInLocalPlayer) {
        map.put(playerID, new Entry(stamina, acceptsInLocalPlayer));
    }

    public void tick() {
        if (syncCooldown > 0) {
            syncCooldown--;
        } else {
            if (!map.isEmpty()) {
                ParCool.getConnection().send(PacketDistributor.ALL.noArg(), packToPacket());
            }
            syncCooldown = 20;
        }
    }

    private MultiStaminaPacket packToPacket() {
        var packet = new MultiStaminaPacket();
        for (var entry : map.entrySet()) {
            var inPacket = new StaminaPacket.StoC();
            inPacket.set(entry.getKey(), new StaminaPacket(entry.getValue().stamina), entry.getValue().acceptsInLocalPlayer);
            packet.add(inPacket);
        }
        map.clear();
        return packet;
    }
}
