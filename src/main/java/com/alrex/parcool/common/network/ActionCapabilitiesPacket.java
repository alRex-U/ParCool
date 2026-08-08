package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ActionCapabilitiesPacket(ActionCapabilities capabilities, Target target) {
    public enum Target {
        CAPABILITY, ENABLED_ACTIONS
    }
    public static final IHandler<ActionCapabilitiesPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ActionCapabilitiesPacket actionCapabilitiesPacket, FriendlyByteBuf packet) {
            packet.writeByte(actionCapabilitiesPacket.target.ordinal());
            actionCapabilitiesPacket.capabilities.write(packet);
        }

        @Override
        public ActionCapabilitiesPacket decode(FriendlyByteBuf packet) {
            var target = Target.values()[packet.readByte() % Target.values().length];
            var caps = new ActionCapabilities(ParCool.getActionRegistry());
            caps.read(packet);
            return new ActionCapabilitiesPacket(caps, target);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(ActionCapabilitiesPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(ActionCapabilitiesPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            if (packet.target == Target.CAPABILITY) {
                parkourability.updateActionCapability(packet.capabilities);
            } else {
                parkourability.updateEnabledActions(packet.capabilities);
            }
        }
    };
}
