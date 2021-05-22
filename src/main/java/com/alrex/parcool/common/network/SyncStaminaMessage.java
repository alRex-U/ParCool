package com.alrex.parcool.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaMessage {

	private int stamina = 0;
	private boolean exhausted = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeInt(this.stamina);
		packet.writeBoolean(this.exhausted);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncStaminaMessage decode(PacketBuffer packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
		message.exhausted = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {

		});
		contextSupplier.get().setPacketHandled(true);
	}

}
