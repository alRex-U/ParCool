package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkRegistrar {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        var r = event.registrar(ParCool.getConnectionId());
        r.playBidirectional(
                ActionStatePacket.TYPE,
                ActionStatePacket.HANDLER.codec(),
                ActionStatePacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                ActionStateSetPacket.TYPE,
                ActionStateSetPacket.HANDLER.codec(),
                ActionStateSetPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                MultiActionStateSetPacket.TYPE,
                MultiActionStateSetPacket.HANDLER.codec(),
                MultiActionStateSetPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                StaminaPacket.TYPE,
                StaminaPacket.HANDLER.codec(),
                StaminaPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                MultiStaminaPacket.TYPE,
                MultiStaminaPacket.HANDLER.codec(),
                MultiStaminaPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                ActionCapabilitiesPacket.TYPE,
                ActionCapabilitiesPacket.HANDLER.codec(),
                ActionCapabilitiesPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                EnableActionPacket.TYPE,
                EnableActionPacket.HANDLER.codec(),
                EnableActionPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                RequestUnlockActionPacket.TYPE,
                RequestUnlockActionPacket.HANDLER.codec(),
                RequestUnlockActionPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                ChangeActivationPacket.TYPE,
                ChangeActivationPacket.HANDLER.codec(),
                ChangeActivationPacket.HANDLER.payloadHandler()
        );
        r.playToClient(
                SkilltreePacket.TYPE,
                SkilltreePacket.HANDLER.codec(),
                SkilltreePacket.HANDLER.payloadHandler()
        );
    }
}
