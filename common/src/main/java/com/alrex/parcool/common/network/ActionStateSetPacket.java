package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.architectury.event.ParCoolActionArchEvent;
import com.alrex.parcool.util.NetworkUtil;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ActionStateSetPacket extends MultiComposablePacket<ActionStatePacket> {
    private final UUID playerID;
    private final boolean castedByClient;
    public static final IHandler<ActionStateSetPacket> HANDLER = new Handler(ParCool.resourceLocation("action.set"));

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

    private record Handler(ResourceLocation id) implements IHandler<ActionStateSetPacket> {
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

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(ActionStateSetPacket actionStateSetPacket, NetworkManager.PacketContext context) {
            var player = NetworkUtil.getPlayerInPhysicalServer(actionStateSetPacket.playerID, context);
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(ActionStateSetPacket actionStateSetPacket, NetworkManager.PacketContext context) {
            var player = NetworkUtil.getPlayerInPhysicalClient(actionStateSetPacket.playerID, context, actionStateSetPacket.castedByClient);
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            if (context.getEnvironment() == Env.SERVER) {
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
                        ParCoolActionArchEvent.Start.Pre.EVENT.invoker().onPre(player, action);
                        action.start();
                        ParCoolActionArchEvent.Start.Post.EVENT.invoker().onPost(player, action);
                    } else if (syncEntry.type() == ActionStatePacket.Type.FINISH && action instanceof ContinuableAction continuableAction) {
                        ParCoolActionArchEvent.Finish.Pre.EVENT.invoker().onPre(player, continuableAction);
                        continuableAction.finish();
                        ParCoolActionArchEvent.Finish.Post.EVENT.invoker().onPost(player, continuableAction);
                    }
                }
            }
        }
    }
}
