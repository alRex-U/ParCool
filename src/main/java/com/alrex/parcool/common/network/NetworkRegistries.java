package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkRegistries {
    private static final String PROTOCOL_VERSION = "3.3.0.0";

    @SubscribeEvent
    public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        var r = event.registrar(PROTOCOL_VERSION);
        r.playBidirectional(
                StartBreakfallEventPayload.TYPE,
                StartBreakfallEventPayload.CODEC,
                StartBreakfallEventPayload::handleServer
        );
        r.playBidirectional(
                ActionStatePayload.TYPE,
                ActionStatePayload.CODEC,
                ActionStatePayload::handleServer
        );
        r.playBidirectional(
                LimitationPayload.TYPE,
                LimitationPayload.CODEC,
                LimitationPayload::handleServer
        );
        r.playBidirectional(
                ClientInformationPayload.TYPE,
                ClientInformationPayload.CODEC,
                ClientInformationPayload::handleServer
        );
        r.playBidirectional(
                StaminaPayload.TYPE,
                StaminaPayload.CODEC,
                StaminaPayload::handleServer
        );
        r.playBidirectional(
                StaminaProcessOnServerPayload.TYPE,
                StaminaProcessOnServerPayload.CODEC,
                StaminaProcessOnServerPayload::handleServer
        );
        r.playToClient(
                StaminaBroadcastPayload.TYPE,
                StaminaBroadcastPayload.CODEC,
                StaminaBroadcastPayload::handleClient
        );
        r.playToClient(
                ActionStateBroadcastPayload.TYPE,
                ActionStateBroadcastPayload.CODEC,
                ActionStateBroadcastPayload::handleClient
        );
    }

    @SubscribeEvent
    public static void onRegisterClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload::handleClient);
        event.register(ActionStatePayload.TYPE, ActionStatePayload::handleClient);
        event.register(LimitationPayload.TYPE, LimitationPayload::handleClient);
        event.register(ClientInformationPayload.TYPE, ClientInformationPayload::handleClient);
        event.register(StaminaPayload.TYPE, StaminaPayload::handleClient);
        event.register(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload::handleClient);
    }
}
