package com.alrex.parcool.common.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public abstract class MultiComposablePacket<T> {
    protected final LinkedList<T> msgList = new LinkedList<>();
    protected final IHandler<T> handler;
    private final ResourceLocation id;

    protected MultiComposablePacket(IHandler<T> handler) {
        this.handler = handler;
        this.id = getComposedID(handler);
    }

    protected static ResourceLocation getComposedID(IHandler<?> handler) {
        return new ResourceLocation(handler.id().getNamespace(), handler.id().getPath() + ".c");
    }

    public ResourceLocation id() {
        return this.id;
    }

    public void add(T packet) {
        if (packet == null) return;
        msgList.add(packet);
    }

    protected Collection<T> getSubPacket() {
        return msgList;
    }

    public static <U, V extends MultiComposablePacket<U>> void encode(V msg, FriendlyByteBuf packet) {
        for (var singleMsg : msg.msgList) {
            msg.handler.encode(singleMsg, packet);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> V decode(Supplier<V> msg, FriendlyByteBuf packet) {
        var instance = msg.get();
        instance.add(instance.handler.decode(packet));
        return instance;
    }

    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalClient(V msg, NetworkManager.PacketContext context) {
        for (var singleMsg : msg.msgList) {
            msg.handler.handleInPhysicalClient(singleMsg, context);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalServer(V msg, NetworkManager.PacketContext context) {
        for (var singleMsg : msg.msgList) {
            msg.handler.handleInPhysicalServer(singleMsg, context);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> void receiveInPhysicalClient(Supplier<V> msgProvider, FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        var msg = decode(msgProvider, buf);
        context.queue(() -> handleInPhysicalClient(msg, context));
    }

    public static <U, V extends MultiComposablePacket<U>> void receiveInPhysicalServer(Supplier<V> msgProvider, FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        var msg = decode(msgProvider, buf);
        context.queue(() -> handleInPhysicalClient(msg, context));
    }
}
