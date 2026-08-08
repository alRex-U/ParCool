package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ActionStatePacket(String groupName, List<Entry> entries) {
    public static IHandler<ActionStatePacket> HANDLER = new Handler();

    public enum Type {
        START, DATA, FINISH
    }

    public record Entry(Type type, ActionEntry<?> entry, byte[] data) {
        private void encode(FriendlyByteBuf buffer) {
            buffer.writeByte(type.ordinal())
                    .writeShort(entry.index())
                    .writeShort(data.length)
                    .writeBytes(data);
        }

        private static Entry decode(String groupName, ActionRegistry actionRegistry, FriendlyByteBuf buffer) {
            var type = Type.values()[buffer.readByte()];
            var actionEntry = actionRegistry.getRegisteredGroups().get(groupName).actions().get(buffer.readShort());
            var dataArray = new byte[buffer.readShort()];
            buffer.readBytes(dataArray);
            return new Entry(type, actionEntry, dataArray);
        }
    }

    private static class Handler implements IHandler<ActionStatePacket> {
        @Override
        public void encode(ActionStatePacket actionStatePacket, FriendlyByteBuf packet) {
            packet.writeByte(actionStatePacket.groupName.length());
            packet.writeCharSequence(actionStatePacket.groupName, StandardCharsets.US_ASCII);
            packet.writeShort(actionStatePacket.entries.size());
            for (var entry : actionStatePacket.entries) {
                entry.encode(packet);
            }
        }

        @Override
        public ActionStatePacket decode(FriendlyByteBuf packet) {
            String namespace = packet.readCharSequence(packet.readByte(), StandardCharsets.US_ASCII).toString();
            var entryLength = packet.readShort();
            var list = new ArrayList<Entry>(entryLength);
            for (var i = 0; i < entryLength; i++) {
                list.add(Entry.decode(namespace, ParCool.getActionRegistry(), packet));
            }
            return new ActionStatePacket(namespace, list);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(ActionStatePacket actionStatePacket, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(ActionStatePacket actionStatePacket, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }
    }
}
