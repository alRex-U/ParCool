package com.alrex.parcool.common.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBufAllocator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PacketHelper {
    private static final FriendlyByteBuf POOLED_BUF = new FriendlyByteBuf(ByteBufAllocator.DEFAULT.buffer());

    public static <MSG> void sendTo(ResourceLocation id, MSG instance, Encoder<MSG> handler, Iterable<ServerPlayer> players) {
        POOLED_BUF.clear();
        handler.encode(instance, POOLED_BUF);
        NetworkManager.sendToPlayers(players, id, POOLED_BUF);
        POOLED_BUF.clear();
    }

    public static <MSG> void sendToAll(ResourceLocation id, MSG instance, Encoder<MSG> handler) {
        POOLED_BUF.clear();
        handler.encode(instance, POOLED_BUF);
        NetworkManager.sendToPlayers(players, id, POOLED_BUF);
        POOLED_BUF.clear();
    }

    @Environment(EnvType.CLIENT)
    public static <MSG> void sendToServer(ResourceLocation id, MSG instance, Encoder<MSG> handler) {
        POOLED_BUF.clear();
        handler.encode(instance, POOLED_BUF);
        NetworkManager.sendToServer(id, POOLED_BUF);
    }

    public interface Encoder<MSG> {
        void encode(MSG msg, FriendlyByteBuf packet);
    }
}
