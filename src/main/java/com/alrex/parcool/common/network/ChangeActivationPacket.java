package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record ChangeActivationPacket(boolean value) {
    public static class StoC extends PlayerTargetPacket.StoC<ChangeActivationPacket> {
        public static final StoCHandler<ChangeActivationPacket, ChangeActivationPacket.StoC> HANDLER = getStoCHandler(ParCool.resourceLocation("activate.s2c"), ChangeActivationPacket.StoC::new, ChangeActivationPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<ChangeActivationPacket> {
        public static final CtoSHandler<ChangeActivationPacket, ChangeActivationPacket.CtoS> HANDLER = getCtoSHandler(ParCool.resourceLocation("activate.c2s"), ChangeActivationPacket.CtoS::new, ChangeActivationPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static final PlayerTargetPacket.IPayloadHandler<ChangeActivationPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(ChangeActivationPacket packet, ByteBuf buf) {
            buf.writeBoolean(packet.value);
        }

        @Override
        public ChangeActivationPacket decode(ByteBuf packet) {
            return new ChangeActivationPacket(packet.readBoolean());
        }

        @Override
        public void handleInLogicalServer(ChangeActivationPacket packet, Player target, IPayloadContext context) {
            var parkourability = Parkourability.get(target);
            parkourability.syncActive(packet.value);

            StoC.HANDLER.send(target, packet, false);
        }

        @Override
        public void handleInLogicalClient(ChangeActivationPacket packet, Player target, IPayloadContext context) {
            var parkourability = Parkourability.get(target);
            parkourability.syncActive(packet.value);
        }
    };
}
