package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record ActionCapabilitiesPacket(ActionCapabilities capabilities, Target target) implements CustomPacketPayload {
    public static final Type<ActionCapabilitiesPacket> TYPE = new Type<>(ParCool.resourceLocation("caps"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Target {
        CAPABILITY, ENABLED_ACTIONS
    }
    public static final IHandler<ActionCapabilitiesPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ByteBuf packet, ActionCapabilitiesPacket actionCapabilitiesPacket) {
            packet.writeByte(actionCapabilitiesPacket.target.ordinal());
            actionCapabilitiesPacket.capabilities.write(packet);
        }

        @Override
        public ActionCapabilitiesPacket decode(ByteBuf packet) {
            var target = Target.values()[packet.readByte() % Target.values().length];
            var caps = new ActionCapabilities(ParCool.getActionRegistry());
            caps.read(packet);
            return new ActionCapabilitiesPacket(caps, target);
        }

        @Override
        public void handleInLogicalServer(ActionCapabilitiesPacket packet, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handleInLogicalClient(ActionCapabilitiesPacket packet, IPayloadContext context) {
            var parkourability = Parkourability.get(context.player());
            if (packet.target == Target.CAPABILITY) {
                parkourability.updateActionCapability(packet.capabilities);
            } else {
                parkourability.updateEnabledActions(packet.capabilities);
            }
        }
    };
}
