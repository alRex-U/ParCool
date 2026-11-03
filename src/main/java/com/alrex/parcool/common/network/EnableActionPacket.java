package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EnableActionPacket(ActionEntry<?> action, boolean enable) {
    public static class StoC extends PlayerTargetPacket.StoC<EnableActionPacket> {
        public static final StoCHandler<EnableActionPacket, EnableActionPacket.StoC> HANDLER = getStoCHandler(EnableActionPacket.StoC::new, EnableActionPacket.HANDLER);
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<EnableActionPacket> {
        public static final CtoSHandler<EnableActionPacket, EnableActionPacket.CtoS> HANDLER = getCtoSHandler(EnableActionPacket.CtoS::new, EnableActionPacket.HANDLER);
    }

    public static final PlayerTargetPacket.IPayloadHandler<EnableActionPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(EnableActionPacket packet, FriendlyByteBuf buf) {
            var groupName = packet.action.id().getNamespace();
            buf.writeUtf(groupName);
            buf.writeShort(packet.action.index());
            buf.writeBoolean(packet.enable);
        }

        @Override
        public EnableActionPacket decode(FriendlyByteBuf packet) {
            return new EnableActionPacket(
                    ParCool.getActionRegistry().getRegisteredGroups().get(packet.readUtf()).actions().get(packet.readShort()),
                    packet.readBoolean()
            );
        }

        @Override
        public void handleInLogicalServer(EnableActionPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            var parkourability = Parkourability.get(target);
            parkourability.getEnabledActions().set(packet.action, packet.enable);

            StoC.HANDLER.send(target, packet, false);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(EnableActionPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            var parkourability = Parkourability.get(target);
            parkourability.getEnabledActions().set(packet.action, packet.enable);
        }
    };
}

