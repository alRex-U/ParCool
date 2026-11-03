package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class PlayerTargetPacket<T> {
    public interface IPayloadHandler<MSG> {
        void encode(MSG msg, FriendlyByteBuf packet);

        MSG decode(FriendlyByteBuf packet);

        @OnlyIn(Dist.CLIENT)
        void handleInLogicalClient(MSG t, Player target, Supplier<NetworkEvent.Context> contextSupplier);

        void handleInLogicalServer(MSG t, Player target, Supplier<NetworkEvent.Context> contextSupplier);
    }

    protected T internalPacket;

    public static <PAYLOAD, COMPOSED extends CtoS<PAYLOAD>> CtoSHandler<PAYLOAD, COMPOSED> getCtoSHandler(Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> iHandler) {
        return new CtoSHandler<>(constructor, iHandler);
    }

    public static <PAYLOAD, COMPOSED extends StoC<PAYLOAD>> StoCHandler<PAYLOAD, COMPOSED> getStoCHandler(Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> iHandler) {
        return new StoCHandler<>(constructor, iHandler);
    }

    public static class CtoS<PAYLOAD> extends PlayerTargetPacket<PAYLOAD> {
        public void send() {
            if (internalPacket == null) return;
            ParCool.getConnection().send(PacketDistributor.SERVER.noArg(), this);
        }

        public CtoS<PAYLOAD> set(PAYLOAD internalPacket) {
            this.internalPacket = internalPacket;
            return this;
        }
    }

    public static class StoC<PAYLOAD> extends PlayerTargetPacket<PAYLOAD> {
        protected UUID targetID;
        protected boolean acceptsInLocalPlayer;

        public void send() {
            if (targetID == null || internalPacket == null) return;
            ParCool.getConnection().send(PacketDistributor.ALL.noArg(), this);
        }

        public StoC<PAYLOAD> set(UUID playerID, PAYLOAD internalPacket, boolean acceptsInLocalPlayer) {
            this.targetID = playerID;
            this.internalPacket = internalPacket;
            this.acceptsInLocalPlayer = acceptsInLocalPlayer;
            return this;
        }
    }

    public abstract static class AbstractHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket<PAYLOAD>> implements com.alrex.parcool.common.network.IHandler<COMPOSED> {
        protected final IPayloadHandler<PAYLOAD> internalHandler;
        protected final Supplier<COMPOSED> constructor;

        protected AbstractHandler(Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            this.internalHandler = internalHandler;
            this.constructor = constructor;
        }
    }

    public static class CtoSHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket.CtoS<PAYLOAD>> extends AbstractHandler<PAYLOAD, COMPOSED> {
        protected CtoSHandler(Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            super(constructor, internalHandler);
        }

        @Override
        public void encode(COMPOSED msg, FriendlyByteBuf packet) {
            internalHandler.encode(msg.internalPacket, packet);
        }

        @Override
        public COMPOSED decode(FriendlyByteBuf packet) {
            var composed = constructor.get();
            composed.internalPacket = internalHandler.decode(packet);
            return composed;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(COMPOSED composed, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handleInLogicalServer(COMPOSED composed, Supplier<NetworkEvent.Context> contextSupplier) {
            if (composed.internalPacket == null) return;
            internalHandler.handleInLogicalServer(composed.internalPacket, contextSupplier.get().getSender(), contextSupplier);
        }

        public void send(@Nullable Player player, PAYLOAD payload) {
            constructor.get().set(payload).send();
        }
    }

    public static class StoCHandler<PAYLOAD, COMPOSED extends PlayerTargetPacket.StoC<PAYLOAD>> extends AbstractHandler<PAYLOAD, COMPOSED> {
        protected StoCHandler(Supplier<COMPOSED> constructor, IPayloadHandler<PAYLOAD> internalHandler) {
            super(constructor, internalHandler);
        }

        @Override
        public void encode(COMPOSED msg, FriendlyByteBuf packet) {
            packet.writeLong(msg.targetID.getMostSignificantBits());
            packet.writeLong(msg.targetID.getLeastSignificantBits());
            packet.writeBoolean(msg.acceptsInLocalPlayer);
            internalHandler.encode(msg.internalPacket, packet);
        }

        @Override
        public COMPOSED decode(FriendlyByteBuf packet) {
            var composed = constructor.get();
            composed.targetID = new UUID(packet.readLong(), packet.readLong());
            composed.acceptsInLocalPlayer = packet.readBoolean();
            composed.internalPacket = internalHandler.decode(packet);
            return composed;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(COMPOSED composed, Supplier<NetworkEvent.Context> contextSupplier) {
            if (composed.targetID == null || composed.internalPacket == null) return;

            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            if (player.getUUID().equals(composed.targetID)) {
                if (composed.acceptsInLocalPlayer)
                    internalHandler.handleInLogicalClient(composed.internalPacket, player, contextSupplier);
            } else {
                player = player.level.getPlayerByUUID(composed.targetID);
                if (player != null)
                    internalHandler.handleInLogicalClient(composed.internalPacket, player, contextSupplier);
            }
        }

        @Override
        public void handleInLogicalServer(COMPOSED composed, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        public void send(@Nullable Player player, PAYLOAD payload, boolean acceptsLocalPlayer) {
            if (player == null) return;
            constructor.get().set(player.getUUID(), payload, acceptsLocalPlayer).send();
        }
    }
}
