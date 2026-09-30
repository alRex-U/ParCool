package com.alrex.parcool.common.handlers;

import com.alrex.parcool.server.limitation.Limitations;

import net.minecraft.server.level.ServerPlayer;

public class LoginLogoutHandler {
    public static void onLogoutInServer(ServerPlayer player) {
        Limitations.unload(player.getUUID());
    }
}
