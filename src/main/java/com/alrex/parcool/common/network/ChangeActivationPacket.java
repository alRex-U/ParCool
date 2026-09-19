package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;

public record ChangeActivationPacket(UUID playerID, boolean value, boolean fromClient) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeActivationPacket> TYPE = new CustomPacketPayload.Type<>(ParCool.resourceLocation("activate"));

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<ChangeActivationPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ByteBuf buf, ChangeActivationPacket packet) {
            buf.writeLong(packet.playerID.getMostSignificantBits());
            buf.writeLong(packet.playerID.getLeastSignificantBits());
            buf.writeBoolean(packet.value);
            buf.writeBoolean(packet.fromClient);
        }

        @Override
        public ChangeActivationPacket decode(ByteBuf packet) {
            return new ChangeActivationPacket(new UUID(packet.readLong(), packet.readLong()), packet.readBoolean(), packet.readBoolean());
        }

        @Override
        public void handleInLogicalServer(ChangeActivationPacket packet, IPayloadContext context) {
            var player = NetworkUtil.getPlayerInLogicalServer(packet.playerID, context);
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.syncActive(packet.value);

            PacketDistributor.sendToAllPlayers(packet);
        }

        @Override
        public void handleInLogicalClient(ChangeActivationPacket packet, IPayloadContext context) {
            var player = NetworkUtil.getPlayerInLogicalClient(packet.playerID, context, packet.fromClient);
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.syncActive(packet.value);
        }
    };
}
