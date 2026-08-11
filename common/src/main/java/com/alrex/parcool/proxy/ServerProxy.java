package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.*;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public void registerMessages() {
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                StaminaPacket.HANDLER.id(),
                StaminaPacket.HANDLER::receiveInPhysicalServer
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                MultiStaminaPacket.ID,
                (buf, context) -> MultiComposablePacket.receiveInPhysicalServer(MultiStaminaPacket::new, buf, context)
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                ActionStateSetPacket.HANDLER.id(),
                ActionStateSetPacket.HANDLER::receiveInPhysicalServer
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                ActionStateSetPacket.HANDLER.id(),
                ActionStateSetPacket.HANDLER::receiveInPhysicalServer
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                MultiActionStateSetPacket.ID,
                (buf, context) -> MultiComposablePacket.receiveInPhysicalServer(MultiActionStateSetPacket::new, buf, context)
        );
        NetworkManager.registerReceiver(
                NetworkManager.serverToClient(),
                ActionCapabilitiesPacket.HANDLER.id(),
                ActionCapabilitiesPacket.HANDLER::receiveInPhysicalServer
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                RequestUnlockActionPacket.HANDLER.id(),
                RequestUnlockActionPacket.HANDLER::receiveInPhysicalServer
        );
        NetworkManager.registerReceiver(
                NetworkManager.clientToServer(),
                EnableActionPacket.HANDLER.id(),
                EnableActionPacket.HANDLER::receiveInPhysicalServer
        );
    }
}
