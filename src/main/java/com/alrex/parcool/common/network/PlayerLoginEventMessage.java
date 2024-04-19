package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PlayerLoginEventMessage {
    public void encode(PacketBuffer packet) {
    }

    public static PlayerLoginEventMessage decode(PacketBuffer packet) {
        return new PlayerLoginEventMessage();
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null || !player.isLocalPlayer()) return;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            SyncClientInformationMessage.sync(player, true);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void send(ServerPlayerEntity player) {
        ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PlayerLoginEventMessage());
    }
}
