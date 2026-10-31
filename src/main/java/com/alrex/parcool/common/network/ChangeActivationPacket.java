package com.alrex.parcool.common.network;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChangeActivationPacket(boolean value) {
    public static class StoC extends PlayerTargetPacket.StoC<ChangeActivationPacket> {
        public static final StoCHandler<ChangeActivationPacket, ChangeActivationPacket.StoC> HANDLER = getStoCHandler(ChangeActivationPacket.StoC::new, ChangeActivationPacket.HANDLER);
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<ChangeActivationPacket> {
        public static final CtoSHandler<ChangeActivationPacket, ChangeActivationPacket.CtoS> HANDLER = getCtoSHandler(ChangeActivationPacket.CtoS::new, ChangeActivationPacket.HANDLER);
    }

    public static final PlayerTargetPacket.IPayloadHandler<ChangeActivationPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
        @Override
        public void encode(ChangeActivationPacket changeActivationPacket, FriendlyByteBuf packet) {
            packet.writeBoolean(changeActivationPacket.value);
        }

        @Override
        public ChangeActivationPacket decode(FriendlyByteBuf packet) {
            return new ChangeActivationPacket(packet.readBoolean());
        }

        @Override
        public void handleInLogicalServer(ChangeActivationPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            var parkourability = Parkourability.get(target);
            parkourability.syncActive(packet.value);

            StoC.HANDLER.send(target, packet, false);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInLogicalClient(ChangeActivationPacket packet, Player target, Supplier<NetworkEvent.Context> contextSupplier) {
            var parkourability = Parkourability.get(target);
            parkourability.syncActive(packet.value);
        }
    };
}
