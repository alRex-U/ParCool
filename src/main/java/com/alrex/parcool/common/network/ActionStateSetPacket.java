package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ActionStateSetPacket extends MultiComposablePacket<ActionStatePacket> implements CustomPacketPayload {
    public static final Type<ActionStateSetPacket> TYPE = new Type<>(ParCool.resourceLocation("action.set"));

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final UUID playerID;
    private final boolean castedByClient;
    public static final IHandler<ActionStateSetPacket> HANDLER = new Handler();

    public static ActionStateSetPacket fromClient(UUID playerID) {
        return new ActionStateSetPacket(playerID, true);
    }

    public static ActionStateSetPacket fromServer(UUID playerID) {
        return new ActionStateSetPacket(playerID, false);
    }

    private ActionStateSetPacket(UUID playerID, boolean castedByClient) {
        super();
        this.playerID = playerID;
        this.castedByClient = castedByClient;
    }

    private static class Handler implements IHandler<ActionStateSetPacket> {
        @Override
        public void encode(ByteBuf packet, ActionStateSetPacket actionStateSetPacket) {
            packet.writeLong(actionStateSetPacket.playerID.getMostSignificantBits());
            packet.writeLong(actionStateSetPacket.playerID.getLeastSignificantBits());
            packet.writeBoolean(actionStateSetPacket.castedByClient);
            MultiComposablePacket.encode(actionStateSetPacket, packet, ActionStatePacket.HANDLER);
        }

        @Override
        public ActionStateSetPacket decode(ByteBuf packet) {
            var id = new UUID(packet.readLong(), packet.readLong());
            var byClient = packet.readBoolean();
            return ActionStateSetPacket.decode(new ActionStateSetPacket(id, byClient), packet, ActionStatePacket.HANDLER);
        }

        @Override
        public void handleInLogicalServer(ActionStateSetPacket actionStateSetPacket, IPayloadContext context) {
            var player = NetworkUtil.getPlayerInPhysicalServer(actionStateSetPacket.playerID, context);
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
        }

        @Override
        public void handleInLogicalClient(ActionStateSetPacket actionStateSetPacket, IPayloadContext context) {
            var player = NetworkUtil.getPlayerInPhysicalClient(actionStateSetPacket.playerID, context, actionStateSetPacket.castedByClient);
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            if (context.flow().getReceptionSide() == LogicalSide.SERVER) {
                ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
            }
        }

        private void processPlayer(ActionStateSetPacket packet, Player player) {
            var parkourability = Parkourability.get(player);
            for (var subPacket : packet.getSubPacket()) {
                for (var syncEntry : subPacket.entries()) {
                    var action = parkourability.get(syncEntry.entry());
                    action.getSynchronizedData().acceptPacket(syncEntry);
                    if (syncEntry.type() == ActionStatePacket.Type.START) {
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
                        action.start();
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
                    } else if (syncEntry.type() == ActionStatePacket.Type.FINISH && action instanceof ContinuableAction continuableAction) {
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
                        continuableAction.finish();
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
                    }
                }
            }
        }
    }
}
