package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record StaminaPacket(ReadonlyStamina stamina) {
	public static class StoC extends PlayerTargetPacket.StoC<StaminaPacket> {
		public static final StoCHandler<StaminaPacket, StaminaPacket.StoC> HANDLER = getStoCHandler(StaminaPacket.StoC::new, StaminaPacket.HANDLER);
	}

	public static class CtoS extends PlayerTargetPacket.CtoS<StaminaPacket> {
		public static final CtoSHandler<StaminaPacket, StaminaPacket.CtoS> HANDLER = getCtoSHandler(StaminaPacket.CtoS::new, StaminaPacket.HANDLER);
	}

	private static final PlayerTargetPacket.IPayloadHandler<StaminaPacket> HANDLER = new PlayerTargetPacket.IPayloadHandler<>() {
		@Override
		public void encode(StaminaPacket staminaPacket, FriendlyByteBuf packet) {
			packet.writeDouble(staminaPacket.stamina.value());
			packet.writeDouble(staminaPacket.stamina.max());
			packet.writeBoolean(staminaPacket.stamina.isExhausted());
			packet.writeBoolean(staminaPacket.stamina.imposePenalty());
		}

		@Override
		public StaminaPacket decode(FriendlyByteBuf packet) {
			return new StaminaPacket(new ReadonlyStamina(
					packet.readDouble(),
					packet.readDouble(),
					packet.readBoolean(),
					packet.readBoolean()
			));
		}

		@Override
		public void handleInLogicalServer(StaminaPacket staminaPacket, Player player, Supplier<NetworkEvent.Context> contextSupplier) {
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina, false);
		}

		@OnlyIn(Dist.CLIENT)
		@Override
		public void handleInLogicalClient(StaminaPacket staminaPacket, Player player, Supplier<NetworkEvent.Context> contextSupplier) {
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);
		}
	};
}
