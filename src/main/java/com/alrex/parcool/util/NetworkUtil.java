package com.alrex.parcool.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkUtil {
    @Nullable
    public static Player getPlayerInLogicalClient(UUID playerID, NetworkEvent.Context context) {
        return getPlayerInLogicalClient(playerID, context, true);
    }

    @Nullable
    public static Player getPlayerInLogicalClient(UUID playerID, NetworkEvent.Context context, boolean acceptsOnlySender) {
        var player = context.getSender();
        if (player == null) return null;
        if (player.getUUID().equals(playerID)) {
            return player;
        } else if (acceptsOnlySender) {
            return null;
        }
        return player.getLevel().getPlayerByUUID(playerID);
    }

    @Nullable
    public static Player getPlayerInLogicalServer(UUID playerID, NetworkEvent.Context context) {
        return getPlayerInLogicalServer(playerID, context, true);
    }

    @Nullable
    public static Player getPlayerInLogicalServer(UUID playerID, NetworkEvent.Context context, boolean acceptsOnlySender) {
        var player = context.getSender();
        if (player == null) return null;
        if (player.getUUID().equals(playerID)) {
            return player;
        } else if (acceptsOnlySender) {
            return null;
        }
        return player.getLevel().getPlayerByUUID(playerID);
    }
}
