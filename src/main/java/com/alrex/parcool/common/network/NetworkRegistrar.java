package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkRegistrar {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        var r = event.registrar(ParCool.getConnectionId());
        PlayerActionPacket.CtoS.HANDLER.register(r);
        PlayerActionPacket.StoC.HANDLER.register(r);
        EnableActionPacket.CtoS.HANDLER.register(r);
        EnableActionPacket.StoC.HANDLER.register(r);
        ChangeActivationPacket.CtoS.HANDLER.register(r);
        ChangeActivationPacket.StoC.HANDLER.register(r);
        StaminaPacket.CtoS.HANDLER.register(r);
        StaminaPacket.StoC.HANDLER.register(r);

        r.playToClient(
                MultiStaminaPacket.TYPE,
                MultiStaminaPacket.HANDLER.codec(),
                MultiStaminaPacket.HANDLER.payloadHandler()
        );
        r.playToClient(
                MultiPlayerActionPacket.TYPE,
                MultiPlayerActionPacket.HANDLER.codec(),
                MultiPlayerActionPacket.HANDLER.payloadHandler()
        );
        r.playBidirectional(
                ActionCapabilitiesPacket.TYPE,
                ActionCapabilitiesPacket.HANDLER.codec(),
                ActionCapabilitiesPacket.HANDLER.payloadHandler()
        );
        r.playToServer(
                RequestUnlockActionPacket.TYPE,
                RequestUnlockActionPacket.HANDLER.codec(),
                RequestUnlockActionPacket.HANDLER.payloadHandler()
        );
        r.playToClient(
                SkilltreePacket.TYPE,
                SkilltreePacket.HANDLER.codec(),
                SkilltreePacket.HANDLER.payloadHandler()
        );
    }
}
