package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

public record EnableActionPacket(ActionEntry<?> action, boolean enable) implements CustomPacketPayload {
    public static final Type<EnableActionPacket> TYPE = new Type<>(ParCool.resourceLocation("enable"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<EnableActionPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ByteBuf buf, EnableActionPacket packet) {
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
        public void handleInLogicalServer(EnableActionPacket packet, IPayloadContext context) {
            var player = context.player();
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }

        @Override
        public void handleInLogicalClient(EnableActionPacket packet, IPayloadContext context) {
            var player = context.player();
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }
    };
}

