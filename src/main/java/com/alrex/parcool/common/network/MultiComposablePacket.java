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

    private record DefaultHandler<PAYLOAD extends CustomPacketPayload, COMPOSED extends MultiComposablePacket<PAYLOAD>>(
            Supplier<COMPOSED> constructor,
            IHandler<PAYLOAD> childHandler
    ) implements IHandler<COMPOSED> {
        @Override
        public void encode(ByteBuf packet, COMPOSED msg) {
            MultiComposablePacket.encode(msg, packet, childHandler);
        }

        @Override
        public COMPOSED decode(ByteBuf packet) {
            return MultiComposablePacket.decode(constructor.get(), packet, childHandler);
        }

        @Override
        public void handleInLogicalClient(COMPOSED msg, IPayloadContext context) {
            MultiComposablePacket.handleInLogicalClient(msg, context, childHandler);
        }

        @Override
        public void handleInLogicalServer(COMPOSED msg, IPayloadContext context) {
            MultiComposablePacket.handleInLogicalServer(msg, context, childHandler);
        }
    }

    public static <PAYLOAD extends CustomPacketPayload, COMPOSED extends MultiComposablePacket<PAYLOAD>> void encode(COMPOSED msg, ByteBuf packet, IHandler<PAYLOAD> childHandler) {
        packet.writeInt(msg.msgList.size());
        for (var singleMsg : msg.msgList) {
            childHandler.encode(packet, singleMsg);
        }
    }

    public static <PAYLOAD extends CustomPacketPayload, COMPOSED extends MultiComposablePacket<PAYLOAD>> COMPOSED decode(COMPOSED msg, ByteBuf packet, IHandler<PAYLOAD> childHandler) {
        var composedPacketCount = packet.readInt();
        for (var i = 0; i < composedPacketCount; i++) {
            msg.add(childHandler.decode(packet));
        }
        return msg;
    }


    public static <PAYLOAD extends CustomPacketPayload, COMPOSED extends MultiComposablePacket<PAYLOAD>> void handleInLogicalClient(COMPOSED msg, IPayloadContext context, IHandler<PAYLOAD> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalClient(singleMsg, context);
        }
    }

    public static <PAYLOAD extends CustomPacketPayload, COMPOSED extends MultiComposablePacket<PAYLOAD>> void handleInLogicalServer(COMPOSED msg, IPayloadContext context, IHandler<PAYLOAD> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalServer(singleMsg, context);
        }
    }
}
