package com.alrex.parcool.fabric;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** Аналог net.neoforged.neoforge.network.PacketDistributor: те три метода, что зовёт ParCool. */
public final class PacketDistributor {
    private PacketDistributor() {}

    public static void sendToServer(CustomPacketPayload payload) {
        // Отдельный класс: ClientPlayNetworking тянет за собой Minecraft, а на выделенном
        // сервере этот класс не должен грузиться даже при верификации PacketDistributor.
        ClientSender.send(payload);
    }

    /**
     * Whether the current server registered this payload's channel (false on a proxy lobby without
     * ParCool).
     */
    public static boolean serverAccepts(CustomPacketPayload.Type<?> type) {
        return ClientSender.canSend(type);
    }

    public static void sendToPlayer(Player player, CustomPacketPayload payload) {
        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        MinecraftServer server = ServerHolder.get();
        if (server == null) return;
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static final class ClientSender {
        static void send(CustomPacketPayload payload) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(payload);
        }

        static boolean canSend(CustomPacketPayload.Type<?> type) {
            return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(type);
        }
    }
}
