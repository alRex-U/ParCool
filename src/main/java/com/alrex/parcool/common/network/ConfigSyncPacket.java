package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ConfigSyncPacket(byte[] data) {
    public static ConfigSyncPacket create() {
        return new ConfigSyncPacket(ParCool.getConfig().serializeServerConfig());
    }

    public static final IHandler<ConfigSyncPacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(ConfigSyncPacket packet, FriendlyByteBuf buf) {
            buf.writeByteArray(packet.data);
        }

        @Override
        public ConfigSyncPacket decode(FriendlyByteBuf buf) {
            return new ConfigSyncPacket(buf.readByteArray());
        }

        @Override
        public void handleInLogicalServer(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            throw new UnsupportedOperationException();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(ConfigSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            // The integrated server already shares these config objects with the client.
            if (Minecraft.getInstance().isLocalServer()) return;
            ParCool.getConfig().applySyncedServerConfig(packet.data);
        }
    };
}
