package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.stamina.ReadonlyStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;

public record StaminaPayload(UUID playerID, ReadonlyStamina stamina) implements CustomPacketPayload {
    public static final Type<StaminaPayload> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.stamina"));
    public static final StreamCodec<ByteBuf, StaminaPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            (s) -> s.playerID().getMostSignificantBits(),
            ByteBufCodecs.VAR_LONG,
            (s) -> s.playerID().getLeastSignificantBits(),
            ReadonlyStamina.STREAM_CODEC,
            StaminaPayload::stamina,
            (ms, ls, s) -> new StaminaPayload(new UUID(ms, ls), s)
    );

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player().level().getPlayerByUUID(payload.playerID);
            if (player == null || player.isLocalPlayer()) return;
            player.setData(Attachments.STAMINA, payload.stamina);
        });
    }

    public static void handleServer(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player().level().getPlayerByUUID(payload.playerID);
            if (player == null) return;
            PacketDistributor.sendToAllPlayers(payload);
            player.setData(Attachments.STAMINA, payload.stamina);
        });
    }
}
