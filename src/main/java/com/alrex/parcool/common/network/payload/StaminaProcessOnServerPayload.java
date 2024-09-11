package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.stamina.StaminaType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record StaminaProcessOnServerPayload(StaminaType stamina, int value) implements CustomPacketPayload {
    public static final Type<StaminaProcessOnServerPayload> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.custom_stamina"));
    public static final StreamCodec<ByteBuf, StaminaProcessOnServerPayload> CODEC = StreamCodec.composite(
            StaminaType.STREAM_CODEC,
            StaminaProcessOnServerPayload::stamina,
            ByteBufCodecs.VAR_INT,
            StaminaProcessOnServerPayload::value,
            StaminaProcessOnServerPayload::new
    );

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StaminaProcessOnServerPayload payload, IPayloadContext context) {
    }

    public static void handleServer(StaminaProcessOnServerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            payload.stamina().newHandler().processOnServer(player, payload.value());
        });
    }
}
