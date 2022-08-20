package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ResetFallDistanceMessage {
	private UUID playerID = null;

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static ResetFallDistanceMessage decode(FriendlyByteBuf packet) {
		ResetFallDistanceMessage message = new ResetFallDistanceMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getNetworkManager().getDirection() != PacketFlow.CLIENTBOUND) {
				player = contextSupplier.get().getSender();
				if (player == null) return;
			} else return;
			player.fallDistance = 0;
		});
		contextSupplier.get().setPacketHandled(true);
	}

	//only in Client
	public static void sync(Player player) {
		player.fallDistance = 0;
		ResetFallDistanceMessage message = new ResetFallDistanceMessage();
		message.playerID = player.getUUID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
