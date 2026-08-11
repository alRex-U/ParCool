package com.alrex.parcool.util;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkUtil {
    @Nullable
    public static Player getPlayerInPhysicalClient(UUID playerID, NetworkManager.PacketContext context, boolean castByClient) {
        boolean isInLogicalServer = context.getEnvironment() == Env.SERVER;
        if (isInLogicalServer) {
            var player = context.getPlayer();
            if (player == null) return null;
            if (!player.getUUID().equals(playerID)) {
                return player.getLevel().getPlayerByUUID(playerID);
            }
            return player;
        } else {
            var world = Minecraft.getInstance().level;
            if (world == null) return null;
            var player = world.getPlayerByUUID(playerID);
            if (castByClient && player != null && player.isLocalPlayer()) return null;
            return player;
        }
    }

    @Nullable
    public static Player getPlayerInPhysicalServer(UUID playerID, NetworkManager.PacketContext context) {
        var player = context.getPlayer();
        if (player == null) return null;
        if (player.getUUID().equals(playerID)) {
            return player;
        }
        return player.getLevel().getPlayerByUUID(playerID);
    }
}
