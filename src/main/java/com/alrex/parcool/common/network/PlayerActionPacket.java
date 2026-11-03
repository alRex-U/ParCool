package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ContinuableAction;
import com.alrex.parcool.api.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record PlayerActionPacket(ActionStateSetPacket inPacket) {
    public static class StoC extends PlayerTargetPacket.StoC<PlayerActionPacket> {
        public static final StoCHandler<PlayerActionPacket, PlayerActionPacket.StoC> HANDLER = getStoCHandler(ParCool.resourceLocation("action.sp.s2c"), PlayerActionPacket.StoC::new, PlayerActionPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<PlayerActionPacket> {
        public static final CtoSHandler<PlayerActionPacket, PlayerActionPacket.CtoS> HANDLER = getCtoSHandler(ParCool.resourceLocation("action.sp.c2s"), PlayerActionPacket.CtoS::new, PlayerActionPacket.HANDLER);

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static final PlayerTargetPacket.IPayloadHandler<PlayerActionPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(PlayerActionPacket actionStateSetPacket, ByteBuf packet) {
            ActionStateSetPacket.HANDLER.encode(packet, actionStateSetPacket.inPacket);
        }

        @Override
        public PlayerActionPacket decode(ByteBuf packet) {
            return new PlayerActionPacket(ActionStateSetPacket.HANDLER.decode(packet));
        }

        @Override
        public void handleInLogicalServer(PlayerActionPacket packet, Player target, IPayloadContext context) {
            processPlayer(packet, target);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(target.getUUID(), packet.inPacket, false);
        }

        @Override
        public void handleInLogicalClient(PlayerActionPacket packet, Player target, IPayloadContext context) {
            processPlayer(packet, target);
        }

        private void processPlayer(PlayerActionPacket packet, Player player) {
            var parkourability = Parkourability.get(player);
            for (var subPacket : packet.inPacket.getSubPacket()) {
                for (var syncEntry : subPacket.entries()) {
                    var action = parkourability.get(syncEntry.entry());
                    action.getSynchronizedData().acceptPacket(syncEntry);
                    if (syncEntry.type() == ActionGroupStatePacket.Type.START) {
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
                        action.start();
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
                    } else if (syncEntry.type() == ActionGroupStatePacket.Type.FINISH && action instanceof ContinuableAction continuableAction) {
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
                        continuableAction.finish();
                        NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
                    }
                }
            }
        }
    };
}
