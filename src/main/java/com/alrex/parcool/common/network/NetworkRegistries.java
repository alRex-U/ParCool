package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.*;
import com.alrex.parcool.fabric.IPayloadContext;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Клиентская половина живёт в ClientNetworkRegistries: у Fabric
 * ClientPlayNetworking.Context.player() возвращает LocalPlayer, и одна такая передача в метод с
 * параметром Player заставляла верификатор грузить net.minecraft.client.player.LocalPlayer на
 * выделенном сервере, где его нет.
 */
public class NetworkRegistries {
    public interface Handler<T> {
        void handle(T payload, IPayloadContext context);
    }

    public static void register() {
        PayloadTypeRegistry.playC2S()
                .register(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload.CODEC);
        PayloadTypeRegistry.playS2C()
                .register(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LimitationPayload.TYPE, LimitationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LimitationPayload.TYPE, LimitationPayload.CODEC);
        PayloadTypeRegistry.playC2S()
                .register(ClientInformationPayload.TYPE, ClientInformationPayload.CODEC);
        PayloadTypeRegistry.playS2C()
                .register(ClientInformationPayload.TYPE, ClientInformationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StaminaPayload.TYPE, StaminaPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StaminaPayload.TYPE, StaminaPayload.CODEC);
        PayloadTypeRegistry.playC2S()
                .register(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload.CODEC);
        PayloadTypeRegistry.playS2C()
                .register(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload.CODEC);
        PayloadTypeRegistry.playS2C()
                .register(StaminaBroadcastPayload.TYPE, StaminaBroadcastPayload.CODEC);
        PayloadTypeRegistry.playS2C()
                .register(ActionStateBroadcastPayload.TYPE, ActionStateBroadcastPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                StartBreakfallEventPayload.TYPE, server(StartBreakfallEventPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(
                ActionStatePayload.TYPE, server(ActionStatePayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(
                LimitationPayload.TYPE, server(LimitationPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(
                ClientInformationPayload.TYPE, server(ClientInformationPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(
                StaminaPayload.TYPE, server(StaminaPayload::handleServer));
        ServerPlayNetworking.registerGlobalReceiver(
                StaminaProcessOnServerPayload.TYPE,
                server(StaminaProcessOnServerPayload::handleServer));
    }

    private static <T extends CustomPacketPayload>
            ServerPlayNetworking.PlayPayloadHandler<T> server(Handler<T> handler) {
        return (payload, context) -> handler.handle(payload, wrap(context.player()));
    }

    // Обработчики Fabric API уже выполняются в игровом потоке, поэтому enqueueWork -- просто вызов.
    public static IPayloadContext wrap(Player player) {
        return new IPayloadContext() {
            @Override
            public Player player() {
                return player;
            }

            @Override
            public void enqueueWork(Runnable work) {
                work.run();
            }
        };
    }
}
