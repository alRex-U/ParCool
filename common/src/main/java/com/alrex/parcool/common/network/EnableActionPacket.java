package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record EnableActionPacket(ActionEntry<?> action, boolean enable) {
    private record Handler(ResourceLocation id) implements IHandler<EnableActionPacket> {
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

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(EnableActionPacket packet, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(EnableActionPacket packet, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }
    }

    public static final IHandler<EnableActionPacket> HANDLER = new Handler(ParCool.resourceLocation("enable.action"));
}

