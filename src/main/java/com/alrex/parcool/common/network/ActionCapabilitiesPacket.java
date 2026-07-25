package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionCapabilities;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ActionCapabilitiesPacket(ActionCapabilities capabilities) {
    public static final IHandler<ActionCapabilitiesPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ActionCapabilitiesPacket actionCapabilitiesPacket, FriendlyByteBuf packet) {
            actionCapabilitiesPacket.capabilities.write(packet);
        }

        @Override
        public ActionCapabilitiesPacket decode(FriendlyByteBuf packet) {
            var caps = new ActionCapabilities(ParCool.getActionRegistry());
            caps.read(packet);
            return new ActionCapabilitiesPacket(caps);
        }

        @Override
        public void handleInPhysicalServer(ActionCapabilitiesPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handleInPhysicalClient(ActionCapabilitiesPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.updateActionCapability(packet.capabilities);
        }
    };
}
