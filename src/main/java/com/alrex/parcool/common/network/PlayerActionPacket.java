package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PlayerActionPacket(ActionStateSetPacket inPacket) {
    public static class StoC extends PlayerTargetPacket.StoC<PlayerActionPacket> {
        public static final StoCHandler<PlayerActionPacket, PlayerActionPacket.StoC> HANDLER = getStoCHandler(PlayerActionPacket.StoC::new, PlayerActionPacket.HANDLER);
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<PlayerActionPacket> {
        public static final CtoSHandler<PlayerActionPacket, PlayerActionPacket.CtoS> HANDLER = getCtoSHandler(PlayerActionPacket.CtoS::new, PlayerActionPacket.HANDLER);
    }

    public static final PlayerTargetPacket.IPayloadHandler<PlayerActionPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(PlayerActionPacket actionStateSetPacket, FriendlyByteBuf packet) {
            ActionStateSetPacket.HANDLER.encode(actionStateSetPacket.inPacket, packet);
        }

        @Override
        public PlayerActionPacket decode(FriendlyByteBuf packet) {
            return new PlayerActionPacket(ActionStateSetPacket.HANDLER.decode(packet));
        }

        @Override
        public void handleInLogicalServer(PlayerActionPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            processPlayer(packet, target);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(target.getUUID(), packet.inPacket, false);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(PlayerActionPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            processPlayer(packet, target);
        }

        private void processPlayer(PlayerActionPacket packet, Player player) {
            var parkourability = Parkourability.get(player);
            for (var subPacket : packet.inPacket.getSubPacket()) {
                for (var syncEntry : subPacket.entries()) {
                    var action = parkourability.get(syncEntry.entry());
                    action.getSynchronizedData().acceptPacket(syncEntry);
                    if (syncEntry.type() == ActionGroupStatePacket.Type.START) {
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
                        action.start();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
                    } else if (syncEntry.type() == ActionGroupStatePacket.Type.FINISH && action instanceof ContinuableAction continuableAction) {
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
                        continuableAction.finish();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
                    }
                }
            }
        }
    };
}
