package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record RequestUnlockActionPacket(ActionEntry<?> action) {
    private record Handler(ResourceLocation id) implements IHandler<RequestUnlockActionPacket> {
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

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(RequestUnlockActionPacket packet, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            var parkourability = Parkourability.get(serverPlayer);
            parkourability.getCapabilities().unlock(serverPlayer, packet.action);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(RequestUnlockActionPacket packet, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            var parkourability = Parkourability.get(serverPlayer);
            parkourability.getCapabilities().unlock(serverPlayer, packet.action);
        }
    }

    public static final IHandler<RequestUnlockActionPacket> HANDLER = new Handler(ParCool.resourceLocation("unlock.action"));
}
