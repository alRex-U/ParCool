package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record StartBreakfallEventPayload(boolean justTimed) implements CustomPacketPayload {
    public static final Type<StartBreakfallEventPayload> TYPE
            = new Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.start_breakfall_event"));
    public static final StreamCodec<ByteBuf, StartBreakfallEventPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            StartBreakfallEventPayload::justTimed,
            StartBreakfallEventPayload::new
    );

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StartBreakfallEventPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;

            parkourability.get(BreakfallReady.class).startBreakfall(player, parkourability, payload.justTimed());
        });
    }

    public static void handleServer(StartBreakfallEventPayload payload, IPayloadContext context) {
    }
}
