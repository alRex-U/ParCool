package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ActionStatePacket(String groupName, List<Entry> entries) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ActionStatePacket> TYPE = new CustomPacketPayload.Type<>(ParCool.resourceLocation("action"));

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IHandler<ActionStatePacket> HANDLER = new Handler();

    public enum Type {
        START, DATA, FINISH
    }

    public record Entry(Type type, ActionEntry<?> entry, byte[] data) {
        private void encode(ByteBuf buffer) {
            buffer.writeByte(type.ordinal())
                    .writeShort(entry.index())
                    .writeShort(data.length)
                    .writeBytes(data);
        }

        private static Entry decode(String groupName, ActionRegistry actionRegistry, ByteBuf buffer) {
            var type = Type.values()[buffer.readByte()];
            var actionEntry = actionRegistry.getRegisteredGroups().get(groupName).actions().get(buffer.readShort());
            var dataArray = new byte[buffer.readShort()];
            buffer.readBytes(dataArray);
            return new Entry(type, actionEntry, dataArray);
        }
    }

    private static class Handler implements IHandler<ActionStatePacket> {
        @Override
        public void encode(ByteBuf packet, ActionStatePacket actionStatePacket) {
            packet.writeByte(actionStatePacket.groupName.length());
            packet.writeCharSequence(actionStatePacket.groupName, StandardCharsets.US_ASCII);
            packet.writeShort(actionStatePacket.entries.size());
            for (var entry : actionStatePacket.entries) {
                entry.encode(packet);
            }
        }

        @Override
        public ActionStatePacket decode(ByteBuf packet) {
            String namespace = packet.readCharSequence(packet.readByte(), StandardCharsets.US_ASCII).toString();
            var entryLength = packet.readShort();
            var list = new ArrayList<Entry>(entryLength);
            for (var i = 0; i < entryLength; i++) {
                list.add(Entry.decode(namespace, ParCool.getActionRegistry(), packet));
            }
            return new ActionStatePacket(namespace, list);
        }

        @Override
        public void handleInLogicalServer(ActionStatePacket actionStatePacket, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handleInLogicalClient(ActionStatePacket actionStatePacket, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }
    }
}
