package com.alrex.parcool.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public abstract class MultiComposablePacket<T> {
    protected final LinkedList<T> msgList = new LinkedList<>();

    public void add(T packet) {
        if (packet == null) return;
        msgList.add(packet);
    }

    protected Collection<T> getSubPacket() {
        return msgList;
    }

    protected static <U, V extends MultiComposablePacket<U>> IHandler<V> getDefaultHandler(Supplier<V> constructor, IHandler<U> childHandler) {
        return new DefaultHandler<>(constructor, childHandler);
    }

    private record DefaultHandler<PAYLOAD, COMPOSED extends MultiComposablePacket<PAYLOAD>>(
            Supplier<COMPOSED> constructor,
            IHandler<PAYLOAD> childHandler
    ) implements IHandler<COMPOSED> {
        @Override
        public void encode(COMPOSED msg, FriendlyByteBuf packet) {
            MultiComposablePacket.encode(msg, packet, childHandler);
        }

        @Override
        public COMPOSED decode(FriendlyByteBuf packet) {
            return MultiComposablePacket.decode(constructor.get(), packet, childHandler);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(COMPOSED msg, Supplier<NetworkEvent.Context> contextSupplier) {
            MultiComposablePacket.handleInLogicalClient(msg, contextSupplier, childHandler);
        }

        @Override
        public void handleInLogicalServer(COMPOSED msg, Supplier<NetworkEvent.Context> contextSupplier) {
            MultiComposablePacket.handleInLogicalServer(msg, contextSupplier, childHandler);
        }
    }

    public static <PAYLOAD, COMPOSED extends MultiComposablePacket<PAYLOAD>> void encode(COMPOSED msg, FriendlyByteBuf packet, IHandler<PAYLOAD> childHandler) {
        packet.writeInt(msg.msgList.size());
        for (var singleMsg : msg.msgList) {
            childHandler.encode(singleMsg, packet);
        }
    }

    public static <PAYLOAD, COMPOSED extends MultiComposablePacket<PAYLOAD>> COMPOSED decode(COMPOSED msg, FriendlyByteBuf packet, IHandler<PAYLOAD> childHandler) {
        var composedPacketCount = packet.readInt();
        for (var i = 0; i < composedPacketCount; i++) {
            msg.add(childHandler.decode(packet));
        }
        return msg;
    }

    @OnlyIn(Dist.CLIENT)
    public static <PAYLOAD, COMPOSED extends MultiComposablePacket<PAYLOAD>> void handleInLogicalClient(COMPOSED msg, Supplier<NetworkEvent.Context> contextSupplier, IHandler<PAYLOAD> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalClient(singleMsg, contextSupplier);
        }
    }

    public static <PAYLOAD, COMPOSED extends MultiComposablePacket<PAYLOAD>> void handleInLogicalServer(COMPOSED msg, Supplier<NetworkEvent.Context> contextSupplier, IHandler<PAYLOAD> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInLogicalServer(singleMsg, contextSupplier);
        }
    }
}
