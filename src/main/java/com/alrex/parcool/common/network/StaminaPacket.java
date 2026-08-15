package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.util.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Supplier;

public record StaminaPacket(UUID playerID, boolean fromClient, ReadonlyStamina stamina) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StaminaPacket> TYPE = new CustomPacketPayload.Type<>(ParCool.resourceLocation("stamina"));

    @Nonnull
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final IHandler<StaminaPacket> HANDLER = new IHandler<>() {
		@Override
        public void encode(ByteBuf packet, StaminaPacket staminaPacket) {
            packet.writeLong(staminaPacket.playerID.getMostSignificantBits());
            packet.writeLong(staminaPacket.playerID.getLeastSignificantBits());
			packet.writeBoolean(staminaPacket.fromClient);
			packet.writeDouble(staminaPacket.stamina.value());
			packet.writeDouble(staminaPacket.stamina.max());
			packet.writeBoolean(staminaPacket.stamina.isExhausted());
			packet.writeBoolean(staminaPacket.stamina.imposePenalty());
		}

		@Override
        public StaminaPacket decode(ByteBuf packet) {
			return new StaminaPacket(
                    new UUID(packet.readLong(), packet.readLong()),
					packet.readBoolean(),
					new ReadonlyStamina(packet.readDouble(), packet.readDouble(), packet.readBoolean(), packet.readBoolean())
			);
		}

		@Override
        public void handleInLogicalServer(StaminaPacket staminaPacket, IPayloadContext context) {
            var player = NetworkUtil.getPlayerInPhysicalServer(staminaPacket.playerID, context);
			if (player == null) return;
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
		}

		@Override
        public void handleInLogicalClient(StaminaPacket staminaPacket, IPayloadContext context) {
			var player = NetworkUtil.getPlayerInPhysicalClient(staminaPacket.playerID, context, staminaPacket.fromClient);
			if (player == null) return;
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

            if (context.flow().getReceptionSide() == LogicalSide.SERVER) {
				ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
			}
		}
	};
}
