package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record LimitationPayload(ServerLimitation limitation) implements CustomPacketPayload {
    public static final Type<LimitationPayload> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.limitation"));
    public static final StreamCodec<ByteBuf, LimitationPayload> CODEC = StreamCodec.composite(
            ServerLimitation.STREAM_CODEC,
            LimitationPayload::limitation,
            LimitationPayload::new
    );

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(LimitationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setServerLimitation(payload.limitation());
            parkourability.getActionInfo().updateStaminaType();
        });
    }

    public static void handleServer(LimitationPayload payload, IPayloadContext context) {
    }
}
