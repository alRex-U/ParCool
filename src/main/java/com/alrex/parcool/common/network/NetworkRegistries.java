package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

public class NetworkRegistries {
    private static final String PROTOCOL_VERSION = "3.3.0.0";

    @SubscribeEvent
    public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        var r = event.registrar(PROTOCOL_VERSION);
        r.playBidirectional(
                StartBreakfallEventPayload.TYPE,
                StartBreakfallEventPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        StartBreakfallEventPayload::handleClient,
                        StartBreakfallEventPayload::handleServer
                )
        );
        r.playBidirectional(
                ActionStatePayload.TYPE,
                ActionStatePayload.CODEC,
                new DirectionalPayloadHandler<>(
                        ActionStatePayload::handleClient,
                        ActionStatePayload::handleServer
                )
        );
        r.playBidirectional(
                LimitationPayload.TYPE,
                LimitationPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        LimitationPayload::handleClient,
                        LimitationPayload::handleServer
                )
        );
        r.playBidirectional(
                ClientInformationPayload.TYPE,
                ClientInformationPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        ClientInformationPayload::handleClient,
                        ClientInformationPayload::handleServer
                )
        );
        r.playBidirectional(
                StaminaPayload.TYPE,
                StaminaPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        StaminaPayload::handleClient,
                        StaminaPayload::handleServer
                )
        );
        r.playBidirectional(
                StaminaProcessOnServerPayload.TYPE,
                StaminaProcessOnServerPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        StaminaProcessOnServerPayload::handleClient,
                        StaminaProcessOnServerPayload::handleServer
                )
        );
    }
}
