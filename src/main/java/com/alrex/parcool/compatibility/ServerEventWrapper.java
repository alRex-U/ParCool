package com.alrex.parcool.compatibility;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.server.ServerLifecycleEvent;

public class ServerEventWrapper {
    private ServerLifecycleEvent event;

    public ServerEventWrapper(Event event) {
        this.event = (ServerLifecycleEvent)event;
    }

    public MinecraftServer getServer() {
        return event.getServer();
    }
}
