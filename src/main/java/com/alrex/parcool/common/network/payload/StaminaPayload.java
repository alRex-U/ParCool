package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;
import com.alrex.parcool.fabric.IPayloadContext;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import javax.annotation.Nonnull;

public record StaminaPayload(UUID playerID, ReadonlyStamina stamina)
        implements CustomPacketPayload {
    public static final Type<StaminaPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.stamina"));
    public static final StreamCodec<ByteBuf, StaminaPayload> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    StaminaPayload::playerID,
                    ReadonlyStamina.STREAM_CODEC,
                    StaminaPayload::stamina,
                    StaminaPayload::new);

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void processPlayer(IPayloadContext context) {
        Player player = context.player().level().getPlayerByUUID(this.playerID);
        if (player == null || player.isLocalPlayer()) return;
        player.setAttached(Attachments.STAMINA, this.stamina);
    }

    public static void handleClient(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> payload.processPlayer(context));
    }

    public static void handleServer(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(
                () -> {
                    // playerID is client-supplied: a client may only set its own stamina, never
                    // another player's.
                    Player sender = context.player();
                    sender.setAttached(Attachments.STAMINA, payload.stamina);
                    StaminaSynchronizationBroadcaster.add(sender.getUUID(), payload.stamina);
                });
    }
}
