package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.attachment.common.Parkourability;

import net.minecraft.server.level.ServerPlayer;

public class PlayerCloneHandler {
    public static void onClone(ServerPlayer from, ServerPlayer player, boolean alive) {
        if (!alive) {
            Parkourability pFrom = Parkourability.get(from);
            Parkourability pTo = Parkourability.get(player);
            if (pFrom != null && pTo != null) {
                pTo.CopyFrom(pFrom);
            }
        }
    }
}
