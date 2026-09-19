package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public record ChangeActivationPacket(UUID playerID, boolean value, boolean fromClient) {
    public static final IHandler<ChangeActivationPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ChangeActivationPacket changeActivationPacket, FriendlyByteBuf packet) {
            packet.writeUUID(changeActivationPacket.playerID);
            packet.writeBoolean(changeActivationPacket.value);
            packet.writeBoolean(changeActivationPacket.fromClient);
        }

        @Override
        public ChangeActivationPacket decode(FriendlyByteBuf packet) {
            return new ChangeActivationPacket(packet.readUUID(), packet.readBoolean(), packet.readBoolean());
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(ChangeActivationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = NetworkUtil.getPlayerInPhysicalServer(packet.playerID, contextSupplier.get());
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.syncActive(packet.value);

            ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), packet);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(ChangeActivationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();
            var player = NetworkUtil.getPlayerInPhysicalClient(packet.playerID, context, packet.fromClient);
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.syncActive(packet.value);

            if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
                ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), packet);
            }
        }
    };
}
