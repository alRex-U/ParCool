package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaMessage {

	private int stamina = 0;
	private boolean exhausted = false;
	private UUID playerID = null;

	private void encode(PacketBuffer packet) {
		packet.writeInt(this.stamina);
		packet.writeBoolean(this.exhausted);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static SyncStaminaMessage decode(PacketBuffer packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
		message.exhausted = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {

		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static class MessageRegistry {
		private static final int ID = 9;

		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			ParCool.CHANNEL_INSTANCE.registerMessage(
					ID,
					SyncStaminaMessage.class,
					SyncStaminaMessage::encode,
					SyncStaminaMessage::decode,
					SyncStaminaMessage::handle
			);
		}
	}
}
