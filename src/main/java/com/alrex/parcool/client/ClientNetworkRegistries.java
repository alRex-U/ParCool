package com.alrex.parcool.client;

import com.alrex.parcool.common.network.NetworkRegistries;
import com.alrex.parcool.common.network.payload.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
public class ClientNetworkRegistries {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                StartBreakfallEventPayload.TYPE, client(StartBreakfallEventPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                ActionStatePayload.TYPE, client(ActionStatePayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                LimitationPayload.TYPE, client(LimitationPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                ClientInformationPayload.TYPE, client(ClientInformationPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                StaminaPayload.TYPE, client(StaminaPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                StaminaProcessOnServerPayload.TYPE,
                client(StaminaProcessOnServerPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                StaminaBroadcastPayload.TYPE, client(StaminaBroadcastPayload::handleClient));
        ClientPlayNetworking.registerGlobalReceiver(
                ActionStateBroadcastPayload.TYPE,
                client(ActionStateBroadcastPayload::handleClient));
    }

    private static <T extends CustomPacketPayload>
            ClientPlayNetworking.PlayPayloadHandler<T> client(
                    NetworkRegistries.Handler<T> handler) {
        return (payload, context) ->
                handler.handle(payload, NetworkRegistries.wrap(context.player()));
    }
}
