package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.server.limitation.Limitations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;

public record ClientInformationPayload(UUID playerID, boolean requestLimitation,
                                       ClientSetting information) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientInformationPayload> TYPE
            = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "payload.client_info"));
    public static final StreamCodec<ByteBuf, ClientInformationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            (p) -> p.playerID().getMostSignificantBits(),
            ByteBufCodecs.VAR_LONG,
            (p) -> p.playerID().getLeastSignificantBits(),
            ByteBufCodecs.BOOL,
            ClientInformationPayload::requestLimitation,
            ClientSetting.STREAM_CODEC,
            ClientInformationPayload::information,
            (ms, ls, r, i) -> new ClientInformationPayload(new UUID(ms, ls), r, i)
    );

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ClientInformationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world = context.player().level();
            var player = world.getPlayerByUUID(payload.playerID());
            if (player == null || player.isLocalPlayer()) return;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setClientSetting(payload.information());
        });
    }

    public static void handleServer(ClientInformationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            PacketDistributor.sendToAllPlayers(payload);

            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            if (player instanceof ServerPlayer serverPlayer && payload.requestLimitation()) {
                Limitations.update(serverPlayer);
            }
            parkourability.getActionInfo().setClientSetting(payload.information());
        });
    }
}
