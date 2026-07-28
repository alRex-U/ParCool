package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestUnlockActionPacket(ActionEntry<?> action) {
    public static final IHandler<RequestUnlockActionPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(RequestUnlockActionPacket packet, FriendlyByteBuf buf) {
            var groupName = packet.action.id().getNamespace();
            buf.writeUtf(groupName);
            buf.writeShort(packet.action.index());
        }

        @Override
        public RequestUnlockActionPacket decode(FriendlyByteBuf packet) {
            return new RequestUnlockActionPacket(
                    ParCool.getActionRegistry().getRegisteredGroups().get(packet.readUtf()).actions().get(packet.readShort())
            );
        }

        @Override
        public void handleInPhysicalServer(RequestUnlockActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getCapabilities().unlock(player, packet.action);
        }

        @Override
        public void handleInPhysicalClient(RequestUnlockActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getCapabilities().unlock(player, packet.action);
        }
    };
}
