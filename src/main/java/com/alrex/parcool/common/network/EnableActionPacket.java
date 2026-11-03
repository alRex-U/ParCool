package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

public record EnableActionPacket(ActionEntry<?> action, boolean enable) {
    public static class StoC extends PlayerTargetPacket.StoC<EnableActionPacket> {
        public static final StoCHandler<EnableActionPacket, EnableActionPacket.StoC> HANDLER = getStoCHandler(ParCool.resourceLocation("enable.s2c"), EnableActionPacket.StoC::new, EnableActionPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<EnableActionPacket> {
        public static final CtoSHandler<EnableActionPacket, EnableActionPacket.CtoS> HANDLER = getCtoSHandler(ParCool.resourceLocation("enable.c2s"), EnableActionPacket.CtoS::new, EnableActionPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static final PlayerTargetPacket.IPayloadHandler<EnableActionPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(EnableActionPacket packet, ByteBuf buf) {
            var groupName = packet.action.id().getNamespace();
            buf.writeShort(groupName.length());
            buf.writeCharSequence(groupName, StandardCharsets.US_ASCII);
            buf.writeShort(packet.action.index());
            buf.writeBoolean(packet.enable);
        }

        @Override
        public EnableActionPacket decode(ByteBuf packet) {
            return new EnableActionPacket(
                    ParCool.getActionRegistry().getRegisteredGroups().get(packet.readCharSequence(packet.readShort(), StandardCharsets.UTF_8).toString()).actions().get(packet.readShort()),
                    packet.readBoolean()
            );
        }

        @Override
        public void handleInLogicalServer(EnableActionPacket packet, Player target, IPayloadContext context) {
            var parkourability = Parkourability.get(target);
            parkourability.getEnabledActions().set(packet.action, packet.enable);

            StoC.HANDLER.send(target, packet, false);
        }

        @Override
        public void handleInLogicalClient(EnableActionPacket packet, Player target, IPayloadContext context) {
            var parkourability = Parkourability.get(target);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }
    };
}

