package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EnableActionPacket(ActionEntry<?> action, boolean enable) {
    public static final IHandler<EnableActionPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(EnableActionPacket packet, FriendlyByteBuf buf) {
            var groupName = packet.action.id().getNamespace();
            buf.writeUtf(groupName);
            buf.writeShort(packet.action.index());
            buf.writeBoolean(packet.enable);
        }

        @Override
        public EnableActionPacket decode(FriendlyByteBuf packet) {
            return new EnableActionPacket(
                    ParCool.getActionRegistry().getRegisteredGroups().get(packet.readUtf()).actions().get(packet.readShort()),
                    packet.readBoolean()
            );
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(EnableActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(EnableActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }
    };
}

