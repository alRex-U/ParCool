package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ResetFallDistanceMessage {
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static ResetFallDistanceMessage decode(PacketBuffer packet) {
		ResetFallDistanceMessage message = new ResetFallDistanceMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() != PacketDirection.CLIENTBOUND) {
				player = contextSupplier.get().getSender();
				if (player == null) return;
			} else return;
			player.fallDistance = 0;
		});
		contextSupplier.get().setPacketHandled(true);
	}

	//only in Client
	public static void sync(PlayerEntity player) {
		player.fallDistance = 0;
		ResetFallDistanceMessage message = new ResetFallDistanceMessage();
		message.playerID = player.getUUID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
