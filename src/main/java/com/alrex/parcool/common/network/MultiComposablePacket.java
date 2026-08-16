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

    private record DefaultHandler<U, V extends MultiComposablePacket<U>>(Supplier<V> constructor,
                                                                         IHandler<U> childHandler) implements IHandler<V> {
        @Override
        public void encode(V msg, FriendlyByteBuf packet) {
            MultiComposablePacket.encode(msg, packet, childHandler);
        }

        @Override
        public V decode(FriendlyByteBuf packet) {
            return MultiComposablePacket.decode(constructor.get(), packet, childHandler);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(V msg, Supplier<NetworkEvent.Context> contextSupplier) {
            MultiComposablePacket.handleInPhysicalClient(msg, contextSupplier, childHandler);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(V msg, Supplier<NetworkEvent.Context> contextSupplier) {
            MultiComposablePacket.handleInPhysicalServer(msg, contextSupplier, childHandler);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> void encode(V msg, FriendlyByteBuf packet, IHandler<U> childHandler) {
        packet.writeInt(msg.msgList.size());
        for (var singleMsg : msg.msgList) {
            childHandler.encode(singleMsg, packet);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> V decode(V msg, FriendlyByteBuf packet, IHandler<U> childHandler) {
        var composedPacketCount = packet.readInt();
        for (var i = 0; i < composedPacketCount; i++) {
            msg.add(childHandler.decode(packet));
        }
        return msg;
    }

    @OnlyIn(Dist.CLIENT)
    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalClient(V msg, Supplier<NetworkEvent.Context> contextSupplier, IHandler<U> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInPhysicalClient(singleMsg, contextSupplier);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalServer(V msg, Supplier<NetworkEvent.Context> contextSupplier, IHandler<U> childHandler) {
        for (var singleMsg : msg.msgList) {
            childHandler.handleInPhysicalServer(singleMsg, contextSupplier);
        }
    }
}
