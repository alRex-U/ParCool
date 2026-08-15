package com.alrex.parcool.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public abstract class MultiComposablePacket<T extends CustomPacketPayload> implements CustomPacketPayload {
    protected final LinkedList<T> msgList = new LinkedList<>();

    public void add(T packet) {
        if (packet == null) return;
        msgList.add(packet);
    }

    protected Collection<T> getSubPacket() {
        return msgList;
    }

    protected static <U extends CustomPacketPayload, V extends MultiComposablePacket<U>> IHandler<V> getDefaultHandler(Supplier<V> constructor, IHandler<U> childHandler) {
        return new DefaultHandler<>(constructor, childHandler);
    }

    private record DefaultHandler<U extends CustomPacketPayload, V extends MultiComposablePacket<U>>(
            Supplier<V> constructor, IHandler<U> childHandler) implements IHandler<V> {
        @Override
        public void encode(ByteBuf packet, V msg) {
            MultiComposablePacket.encode(msg, packet, childHandler);
        }

        @Override
        public V decode(ByteBuf packet) {
            return MultiComposablePacket.decode(constructor.get(), packet, childHandler);
        }

        @Override
        public void handleInLogicalClient(V msg, IPayloadContext context) {
            MultiComposablePacket.handleInLogicalClient(msg, context, childHandler);
        }

        @Override
        public void handleInLogicalServer(V msg, IPayloadContext context) {
            MultiComposablePacket.handleInLogicalServer(msg, context, childHandler);
        }
    }

    public static <U extends CustomPacketPayload, V extends MultiComposablePacket<U>> void encode(V msg, ByteBuf packet, IHandler<U> childHandler) {
        packet.writeInt(msg.msgList.size());
        for (var singleMsg : msg.msgList) {
            childHandler.encode(packet, singleMsg);
        }
    }

    public static <U extends CustomPacketPayload, V extends MultiComposablePacket<U>> V decode(V msg, ByteBuf packet, IHandler<U> childHandler) {
        var composedPacketCount = packet.readInt();
        for (var i = 0; i < composedPacketCount; i++) {
            msg.add(childHandler.decode(packet));
        }
        return msg;
    }


    public static <U extends CustomPacketPayload, V extends MultiComposablePacket<U>> void handleInLogicalClient(V msg, IPayloadContext context, IHandler<U> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalClient(singleMsg, context);
        }
    }

    public static <U extends CustomPacketPayload, V extends MultiComposablePacket<U>> void handleInLogicalServer(V msg, IPayloadContext context, IHandler<U> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalServer(singleMsg, context);
        }
    }
}
