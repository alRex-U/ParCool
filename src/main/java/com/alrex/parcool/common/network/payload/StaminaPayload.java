package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;

public record StaminaPayload(UUID playerID, ReadonlyStamina stamina) implements CustomPacketPayload {
    public static final Type<StaminaPayload> TYPE
            = new Type<>(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "payload.stamina"));
    public static final StreamCodec<ByteBuf, StaminaPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            StaminaPayload::playerID,
            ReadonlyStamina.STREAM_CODEC,
            StaminaPayload::stamina,
            StaminaPayload::new
    );

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void processPlayer(IPayloadContext context) {
        Player player = context.player().level().getPlayerByUUID(this.playerID);
        if (player == null || player.isLocalPlayer()) return;
        player.setData(Attachments.STAMINA, this.stamina);
    }

    public static void handleClient(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> payload.processPlayer(context));
    }

    public static void handleServer(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            payload.processPlayer(context);
            StaminaSynchronizationBroadcaster.add(payload.playerID, payload.stamina);
        });
    }
}
