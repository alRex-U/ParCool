package com.alrex.parcool.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

/**
 * У NeoForge сервер доставали из ServerLifecycleHooks; под Fabric ловим его событиями жизненного
 * цикла.
 */
public final class ServerHolder {
    private static volatile MinecraftServer server = null;

    private ServerHolder() {}

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
    }

    @Nullable
    public static MinecraftServer get() {
        return server;
    }
}
