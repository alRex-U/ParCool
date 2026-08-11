package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionCapabilities;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ActionCapabilitiesPacket(ActionCapabilities capabilities, Target target) {
    public enum Target {
        CAPABILITY, ENABLED_ACTIONS
    }

    private record Handler(ResourceLocation id) implements IHandler<ActionCapabilitiesPacket> {
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

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(ActionCapabilitiesPacket packet, NetworkManager.PacketContext context) {
            throw new UnsupportedOperationException();
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(ActionCapabilitiesPacket packet, NetworkManager.PacketContext context) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            if (packet.target == Target.CAPABILITY) {
                parkourability.updateActionCapability(packet.capabilities);
            } else {
                parkourability.updateEnabledActions(packet.capabilities);
            }
        }
    }

    public static final IHandler<ActionCapabilitiesPacket> HANDLER = new Handler(ParCool.resourceLocation("sync.action.caps"));
}
