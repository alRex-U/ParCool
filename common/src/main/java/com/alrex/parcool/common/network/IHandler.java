package com.alrex.parcool.common.network;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IHandler<MSG> {
    void encode(MSG msg, FriendlyByteBuf packet);

    MSG decode(FriendlyByteBuf packet);

    @Environment(EnvType.SERVER)
    void handleInPhysicalServer(MSG msg, NetworkManager.PacketContext context);

    @Environment(EnvType.CLIENT)
    void handleInPhysicalClient(MSG msg, NetworkManager.PacketContext context);

    ResourceLocation id();

    @Environment(EnvType.SERVER)
    default void receiveInPhysicalServer(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        context.queue(() -> handleInPhysicalServer(this.decode(buf), context));
    }

    @Environment(EnvType.CLIENT)
    default void receiveInPhysicalClient(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        context.queue(() -> handleInPhysicalClient(this.decode(buf), context));
    }
}
