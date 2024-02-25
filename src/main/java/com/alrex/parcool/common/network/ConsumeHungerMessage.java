package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ConsumeHungerMessage {
    private float value;

    public void encode(FriendlyByteBuf packet) {
        packet.writeFloat(value);
    }

    public static ConsumeHungerMessage decode(FriendlyByteBuf packet) {
        ConsumeHungerMessage message = new ConsumeHungerMessage();
        message.value = packet.readFloat();
        return message;
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = contextSupplier.get().getSender();
            if (serverPlayer == null) return;
            serverPlayer.causeFoodExhaustion(value);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void send(Player player, float value) {
        if (!player.isLocalPlayer()) return;
        ConsumeHungerMessage message = new ConsumeHungerMessage();
        message.value = value;
        ParCool.CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }
}
