package com.alrex.parcool.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class PlayerTargetPacket<T> {
    public interface IPayloadHandler<MSG> {
        void encode(MSG msg, ByteBuf packet);

        MSG decode(ByteBuf packet);

        void handleInLogicalClient(MSG t, Player target, IPayloadContext context);

        void handleInLogicalServer(MSG t, Player target, IPayloadContext context);
    }

    protected T internalPacket;

    public static <PAYLOAD, COMPOSED extends CtoS<PAYLOAD>> CtoSHandler<PAYLOAD, COMPOSED> getCtoSHandler(ResourceLocation id, Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> iHandler) {
        return new CtoSHandler<>(id, constructor, iHandler);
    }

    public static <PAYLOAD, COMPOSED extends StoC<PAYLOAD>> StoCHandler<PAYLOAD, COMPOSED> getStoCHandler(ResourceLocation id, Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> iHandler) {
        return new StoCHandler<>(id, constructor, iHandler);
    }

    public static abstract class CtoS<PAYLOAD> extends PlayerTargetPacket<PAYLOAD> implements CustomPacketPayload {
        public void send() {
            if (internalPacket == null) return;
            PacketDistributor.sendToServer(this);
        }

        public CtoS<PAYLOAD> set(PAYLOAD internalPacket) {
            this.internalPacket = internalPacket;
            return this;
        }
    }

    public static abstract class StoC<PAYLOAD> extends PlayerTargetPacket<PAYLOAD> implements CustomPacketPayload {
        protected UUID targetID;
        protected boolean acceptsInLocalPlayer;

        public void send() {
            if (targetID == null || internalPacket == null) return;
            PacketDistributor.sendToAllPlayers(this);
        }

        public StoC<PAYLOAD> set(UUID playerID, PAYLOAD internalPacket, boolean acceptsInLocalPlayer) {
            this.targetID = playerID;
            this.internalPacket = internalPacket;
            this.acceptsInLocalPlayer = acceptsInLocalPlayer;
            return this;
        }
    }

    public abstract static class AbstractHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket<PAYLOAD> & CustomPacketPayload> implements com.alrex.parcool.common.network.IHandler<COMPOSED> {
        protected final IPayloadHandler<PAYLOAD> internalHandler;
        protected final Supplier<COMPOSED> constructor;
        protected final CustomPacketPayload.Type<COMPOSED> type;

        public CustomPacketPayload.Type<COMPOSED> type() {
            return type;
        }

        protected AbstractHandler(ResourceLocation id, Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            this.internalHandler = internalHandler;
            this.constructor = constructor;
            type = new CustomPacketPayload.Type<>(id);
        }

        public abstract void register(PayloadRegistrar registrar);
    }

    public static class CtoSHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket.CtoS<PAYLOAD>> extends AbstractHandler<PAYLOAD, COMPOSED> {
        protected CtoSHandler(ResourceLocation id, Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            super(id, constructor, internalHandler);
        }

        @Override
        public void encode(ByteBuf packet, COMPOSED composed) {
            internalHandler.encode(composed.internalPacket, packet);
        }

        @Override
        public COMPOSED decode(ByteBuf packet) {
            var composed = constructor.get();
            composed.internalPacket = internalHandler.decode(packet);
            return composed;
        }

        @Override
        public void handleInLogicalClient(COMPOSED composed, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handleInLogicalServer(COMPOSED composed, IPayloadContext context) {
            if (composed.internalPacket == null) return;
            internalHandler.handleInLogicalServer(composed.internalPacket, context.player(), context);
        }

        public void send(@Nullable Player player, PAYLOAD payload) {
            constructor.get().set(payload).send();
        }

        @Override
        public void register(PayloadRegistrar registrar) {
            registrar.playToServer(this.type(), this.codec(), this.payloadHandler());
        }
    }

    public static class StoCHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket.StoC<PAYLOAD>> extends AbstractHandler<PAYLOAD, COMPOSED> {
        protected StoCHandler(ResourceLocation id, Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            super(id, constructor, internalHandler);
        }

        @Override
        public void encode(ByteBuf packet, COMPOSED msg) {
            packet.writeLong(msg.targetID.getMostSignificantBits());
            packet.writeLong(msg.targetID.getLeastSignificantBits());
            packet.writeBoolean(msg.acceptsInLocalPlayer);
            internalHandler.encode(msg.internalPacket, packet);
        }

        @Override
        public COMPOSED decode(ByteBuf packet) {
            var composed = constructor.get();
            composed.targetID = new UUID(packet.readLong(), packet.readLong());
            composed.acceptsInLocalPlayer = packet.readBoolean();
            composed.internalPacket = internalHandler.decode(packet);
            return composed;
        }

        @Override
        public void handleInLogicalClient(COMPOSED composed, IPayloadContext context) {
            if (composed.targetID == null || composed.internalPacket == null) return;

            Player player = context.player();
            if (player.getUUID().equals(composed.targetID)) {
                if (composed.acceptsInLocalPlayer)
                    internalHandler.handleInLogicalClient(composed.internalPacket, player, context);
            } else {
                player = player.level().getPlayerByUUID(composed.targetID);
                if (player != null)
                    internalHandler.handleInLogicalClient(composed.internalPacket, player, context);
            }
        }

        @Override
        public void handleInLogicalServer(COMPOSED composed, IPayloadContext context) {
            throw new UnsupportedOperationException();
        }

        public void send(@Nullable Player player, PAYLOAD payload, boolean acceptsLocalPlayer) {
            if (player == null) return;
            constructor.get().set(player.getUUID(), payload, acceptsLocalPlayer).send();
        }

        @Override
        public void register(PayloadRegistrar registrar) {
            registrar.playToClient(this.type(), this.codec(), this.payloadHandler());
        }
    }
}
