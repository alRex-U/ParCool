package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.ListStreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record StaminaBroadcastPayload(List<StaminaPayload> staminaList) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StaminaBroadcastPayload> TYPE
            = new Type<>(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "payload.stamina.broadcast"));

    private static final StreamCodec<ByteBuf, List<StaminaPayload>> STAMINA_CODEC = new ListStreamCodec<>(StaminaPayload.CODEC);
    public static final StreamCodec<ByteBuf, StaminaBroadcastPayload> CODEC = StreamCodec.composite(
            STAMINA_CODEC,
            StaminaBroadcastPayload::staminaList,
            StaminaBroadcastPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StaminaBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (var elem : payload.staminaList()) {
                elem.processPlayer(context);
            }
        });
    }
}
