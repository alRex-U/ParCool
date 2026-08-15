package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

public record RequestUnlockActionPacket(ActionEntry<?> action) implements CustomPacketPayload {
    public static final Type<RequestUnlockActionPacket> TYPE = new Type<>(ParCool.resourceLocation("unlock"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<RequestUnlockActionPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ByteBuf buf, RequestUnlockActionPacket packet) {
            var groupName = packet.action.id().getNamespace();
            buf.writeShort(groupName.length());
            buf.writeCharSequence(groupName, StandardCharsets.US_ASCII);
            buf.writeShort(packet.action.index());
        }

        @Override
        public RequestUnlockActionPacket decode(ByteBuf packet) {
            return new RequestUnlockActionPacket(
                    ParCool.getActionRegistry().getRegisteredGroups().get(packet.readCharSequence(packet.readShort(), StandardCharsets.US_ASCII).toString()).actions().get(packet.readShort())
            );
        }

        @Override
        public void handleInLogicalServer(RequestUnlockActionPacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            var parkourability = Parkourability.get(serverPlayer);
            parkourability.getCapabilities().unlock(serverPlayer, packet.action);
        }

        @Override
        public void handleInLogicalClient(RequestUnlockActionPacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            var parkourability = Parkourability.get(serverPlayer);
            parkourability.getCapabilities().unlock(serverPlayer, packet.action);
        }
    };
}
