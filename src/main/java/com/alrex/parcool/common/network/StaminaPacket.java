package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record StaminaPacket(UUID playerID, boolean fromClient, ReadonlyStamina stamina) {
    public static final IHandler<StaminaPacket> HANDLER = new IHandler<>() {
		@Override
		public void encode(StaminaPacket staminaPacket, FriendlyByteBuf packet) {
			packet.writeUUID(staminaPacket.playerID);
			packet.writeBoolean(staminaPacket.fromClient);
			packet.writeDouble(staminaPacket.stamina.value());
			packet.writeDouble(staminaPacket.stamina.max());
			packet.writeBoolean(staminaPacket.stamina.isExhausted());
			packet.writeBoolean(staminaPacket.stamina.imposePenalty());
		}

		@Override
		public StaminaPacket decode(FriendlyByteBuf packet) {
			return new StaminaPacket(
					packet.readUUID(),
					packet.readBoolean(),
					new ReadonlyStamina(packet.readDouble(), packet.readDouble(), packet.readBoolean(), packet.readBoolean())
			);
		}

		@OnlyIn(Dist.DEDICATED_SERVER)
		@Override
		public void handleInPhysicalServer(StaminaPacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
			var player = NetworkUtil.getPlayerInPhysicalServer(staminaPacket.playerID, contextSupplier.get());
			if (player == null) return;
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
		}

		@OnlyIn(Dist.CLIENT)
		@Override
		public void handleInPhysicalClient(StaminaPacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
			var context = contextSupplier.get();
			var player = NetworkUtil.getPlayerInPhysicalClient(staminaPacket.playerID, context, staminaPacket.fromClient);
			if (player == null) return;
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
				ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
			}
		}
	};
}
