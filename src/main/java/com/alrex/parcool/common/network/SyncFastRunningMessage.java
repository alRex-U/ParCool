package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IFastRunning;
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

public class SyncFastRunningMessage {
	private boolean isFastRunning = false;
	private UUID playerID = null;

	private void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isFastRunning);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static SyncFastRunningMessage decode(PacketBuffer packet) {
		SyncFastRunningMessage message = new SyncFastRunningMessage();
		message.isFastRunning = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				player = Minecraft.getInstance().world.getPlayerByUuid(playerID);

			} else {
				player = contextSupplier.get().getSender();
			}
			IFastRunning fastRunning = IFastRunning.get(player);
			fastRunning.setFastRunning(this.isFastRunning);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	//only in Client
	public static void sync(ClientPlayerEntity player) {
		IFastRunning fastRunning = IFastRunning.get(player);

		SyncFastRunningMessage message = new SyncFastRunningMessage();
		message.isFastRunning = fastRunning.isFastRunning();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

	public static class MessageRegistry {
		private static final int ID = 6;

		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			ParCool.CHANNEL_INSTANCE.registerMessage(
					ID,
					SyncFastRunningMessage.class,
					SyncFastRunningMessage::encode,
					SyncFastRunningMessage::decode,
					SyncFastRunningMessage::handle
			);
		}
	}
}
