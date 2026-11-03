package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StaminaPacket(ReadonlyStamina stamina) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StaminaPacket> TYPE = new CustomPacketPayload.Type<>(ParCool.resourceLocation("stamina"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class StoC extends PlayerTargetPacket.StoC<StaminaPacket> {
        public static final StoCHandler<StaminaPacket, StaminaPacket.StoC> HANDLER = getStoCHandler(ParCool.resourceLocation("stamina.s2c"), StaminaPacket.StoC::new, StaminaPacket.HANDLER);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static class CtoS extends PlayerTargetPacket.CtoS<StaminaPacket> {
        public static final CtoSHandler<StaminaPacket, StaminaPacket.CtoS> HANDLER = getCtoSHandler(ParCool.resourceLocation("stamina.c2s"), StaminaPacket.CtoS::new, StaminaPacket.HANDLER);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return HANDLER.type;
        }
    }

    public static final PlayerTargetPacket.IPayloadHandler<StaminaPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<StaminaPacket>() {
        @Override
        public void encode(StaminaPacket staminaPacket, ByteBuf packet) {
			packet.writeDouble(staminaPacket.stamina.value());
			packet.writeDouble(staminaPacket.stamina.max());
			packet.writeBoolean(staminaPacket.stamina.isExhausted());
			packet.writeBoolean(staminaPacket.stamina.imposePenalty());
		}

		@Override
        public StaminaPacket decode(ByteBuf packet) {
            return new StaminaPacket(new ReadonlyStamina(
                    packet.readDouble(),
                    packet.readDouble(),
					packet.readBoolean(),
                    packet.readBoolean()
            ));
		}

		@Override
        public void handleInLogicalServer(StaminaPacket staminaPacket, Player player, IPayloadContext context) {
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

            ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina, false);
		}

		@Override
        public void handleInLogicalClient(StaminaPacket staminaPacket, Player player, IPayloadContext context) {
            var parkourability = Parkourability.get(player);
            parkourability.updateStaminaInRemote(staminaPacket.stamina);
		}
	};
}
