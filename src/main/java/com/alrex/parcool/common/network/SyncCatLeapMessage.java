package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICatLeap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncCatLeapMessage {
	private boolean isLeaping = false;
	private UUID playerID = null;

	private void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isLeaping);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static SyncCatLeapMessage decode(PacketBuffer packet) {
		SyncCatLeapMessage message = new SyncCatLeapMessage();
		message.isLeaping = packet.readBoolean();
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
			LazyOptional<ICatLeap> catLeapOptional = player.getCapability(ICatLeap.CatLeapProvider.CAT_LEAP_CAPABILITY);
			if (!catLeapOptional.isPresent()) return;
			ICatLeap catLeap = catLeapOptional.resolve().get();
			catLeap.setLeaping(this.isLeaping);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerEntity player) {
		LazyOptional<ICatLeap> fastOptional = player.getCapability(ICatLeap.CatLeapProvider.CAT_LEAP_CAPABILITY);
		if (!fastOptional.isPresent()) return;
		ICatLeap catLeap = fastOptional.resolve().get();

		SyncCatLeapMessage message = new SyncCatLeapMessage();
		message.isLeaping = catLeap.isLeaping();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class MessageRegistry {
		private static final int ID = 4;

		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			ParCool.CHANNEL_INSTANCE.registerMessage(
					ID,
					SyncCatLeapMessage.class,
					SyncCatLeapMessage::encode,
					SyncCatLeapMessage::decode,
					SyncCatLeapMessage::handle
			);
		}
	}
}
