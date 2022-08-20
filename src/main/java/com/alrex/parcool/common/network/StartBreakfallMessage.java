package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartBreakfallMessage {
	UUID playerID = null;

	public UUID getPlayerID() {
		return playerID;
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
	}

	public static StartBreakfallMessage decode(FriendlyByteBuf packet) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketFlow.CLIENTBOUND) {
				Player player = Minecraft.getInstance().player;
				if (player == null) return;
				if (!playerID.equals(player.getUUID())) return;

				Parkourability parkourability = Parkourability.get(player);
				if (parkourability == null) return;
				Stamina stamina = Stamina.get(player);
				if (stamina == null) return;

				parkourability.getBreakfall().startBreakfall(player, parkourability, stamina);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
	}

	public static void send(ServerPlayer player) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = player.getUUID();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
