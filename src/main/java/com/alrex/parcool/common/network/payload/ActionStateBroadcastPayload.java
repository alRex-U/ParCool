package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.ListStreamCodec;
import com.alrex.parcool.fabric.IPayloadContext;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public record ActionStateBroadcastPayload(List<ActionStatePayload> payloads)
        implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ActionStateBroadcastPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            ParCool.MOD_ID, "payload.action_state.broadcast"));

    public static final StreamCodec<ByteBuf, ActionStateBroadcastPayload> CODEC =
            StreamCodec.composite(
                    new ListStreamCodec<>(ActionStatePayload.CODEC),
                    ActionStateBroadcastPayload::payloads,
                    ActionStateBroadcastPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ActionStateBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(
                () -> {
                    Player player;
                    Level world = context.player().level();
                    for (var statePayload : payload.payloads()) {
                        player = world.getPlayerByUUID(statePayload.playerID());
                        // continue, not return: the batch holds every player's states, including
                        // our own
                        // and those of players in other dimensions; one of them must not drop the
                        // rest.
                        if (player == null || player.isLocalPlayer()) continue;

                        statePayload.processPlayer(player);
                    }
                });
    }
}
