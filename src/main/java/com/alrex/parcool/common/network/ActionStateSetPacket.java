package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ActionStateSetPacket extends MultiComposablePacket<ActionStatePacket> {
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
        super(ActionStatePacket.HANDLER);
        this.playerID = playerID;
        this.castedByClient = castedByClient;
    }

    private static class Handler implements IHandler<ActionStateSetPacket> {
        @Override
        public void encode(ActionStateSetPacket actionStateSetPacket, FriendlyByteBuf packet) {
            packet.writeUUID(actionStateSetPacket.playerID);
            packet.writeBoolean(actionStateSetPacket.castedByClient);
            MultiComposablePacket.encode(actionStateSetPacket, packet);
        }

        @Override
        public ActionStateSetPacket decode(FriendlyByteBuf packet) {
            var id = packet.readUUID();
            var byClient = packet.readBoolean();
            return ActionStateSetPacket.decode(() -> new ActionStateSetPacket(id, byClient), packet);
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(ActionStateSetPacket actionStateSetPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = NetworkUtil.getPlayerInPhysicalServer(actionStateSetPacket.playerID, contextSupplier.get());
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(ActionStateSetPacket actionStateSetPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();
            var player = NetworkUtil.getPlayerInPhysicalClient(actionStateSetPacket.playerID, context, actionStateSetPacket.castedByClient);
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
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
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
                        action.start();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
                    } else if (syncEntry.type() == ActionStatePacket.Type.FINISH && action instanceof ContinuableAction continuableAction) {
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
                        continuableAction.finish();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
                    }
                }
            }
        }
    }
}
