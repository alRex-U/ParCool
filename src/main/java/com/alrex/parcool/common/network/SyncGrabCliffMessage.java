package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IGrabCliff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncGrabCliffMessage {
	private boolean isGrabbing = false;
	private UUID playerID = null;

	private void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isGrabbing);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static SyncGrabCliffMessage decode(PacketBuffer packet) {
		SyncGrabCliffMessage message = new SyncGrabCliffMessage();
		message.isGrabbing = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
				if (clientPlayer == null || playerID.equals(clientPlayer.getUniqueID())) return;
				player = clientPlayer.world.getPlayerByUuid(playerID);
			} else {
				player = contextSupplier.get().getSender();
			}
			IGrabCliff grabCliff = IGrabCliff.get(player);
			if (grabCliff == null) return;

			grabCliff.setGrabbing(this.isGrabbing);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	//only in Client
	public static void sync(ClientPlayerEntity player) {
		IGrabCliff grabCliff = IGrabCliff.get(player);

		SyncGrabCliffMessage message = new SyncGrabCliffMessage();
		message.isGrabbing = grabCliff.isGrabbing();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

	public static class MessageRegistry {
		private static final int ID = 7;

		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			ParCool.CHANNEL_INSTANCE.registerMessage(
					ID,
					SyncGrabCliffMessage.class,
					SyncGrabCliffMessage::encode,
					SyncGrabCliffMessage::decode,
					SyncGrabCliffMessage::handle
			);
		}
	}
}

