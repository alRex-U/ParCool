package com.alrex.parcool.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.UUID;

public class NetworkUtil {
    @Nullable
    public static Player getPlayerInPhysicalClient(UUID playerID, IPayloadContext context, boolean castByClient) {
        boolean isInLogicalServer = context.flow().getReceptionSide() == LogicalSide.SERVER;
        if (isInLogicalServer) {
            var player = context.player();
            if (!player.getUUID().equals(playerID)) {
                return player.level().getPlayerByUUID(playerID);
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
    public static Player getPlayerInPhysicalServer(UUID playerID, IPayloadContext context) {
        var player = context.player();
        if (player.getUUID().equals(playerID)) {
            return player;
        }
        return player.level().getPlayerByUUID(playerID);
    }
}
