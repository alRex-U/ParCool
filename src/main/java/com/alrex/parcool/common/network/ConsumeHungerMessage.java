package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ConsumeHungerMessage {
    private float value;

    public void encode(PacketBuffer packet) {
        packet.writeFloat(value);
    }

    public static ConsumeHungerMessage decode(PacketBuffer packet) {
        ConsumeHungerMessage message = new ConsumeHungerMessage();
        message.value = packet.readFloat();
        return message;
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity serverPlayer = contextSupplier.get().getSender();
            if (serverPlayer == null) return;
            serverPlayer.causeFoodExhaustion(value);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void send(PlayerEntity player, float value) {
        if (!player.isLocalPlayer()) return;
        ConsumeHungerMessage message = new ConsumeHungerMessage();
        message.value = value;
        ParCool.CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }
}
