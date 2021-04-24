package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IDodge;
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

public class SyncDodgeMessage {
	private UUID playerID = null;
	private boolean isDodging = false;

	private void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isDodging);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static SyncDodgeMessage decode(PacketBuffer packet) {
		SyncDodgeMessage message = new SyncDodgeMessage();
		message.isDodging = packet.readBoolean();
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
			if (player == null) return;
			LazyOptional<IDodge> dodgeOptional = player.getCapability(IDodge.DodgeProvider.DODGE_CAPABILITY);
			if (!dodgeOptional.isPresent()) return;
			IDodge dodge = dodgeOptional.resolve().get();
			dodge.setDodging(this.isDodging);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerEntity player) {
		LazyOptional<IDodge> fastOptional = player.getCapability(IDodge.DodgeProvider.DODGE_CAPABILITY);
		if (!fastOptional.isPresent()) return;
		IDodge Dodge = fastOptional.resolve().get();

		SyncDodgeMessage message = new SyncDodgeMessage();
		message.isDodging = Dodge.isDodging();
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
					SyncDodgeMessage.class,
					SyncDodgeMessage::encode,
					SyncDodgeMessage::decode,
					SyncDodgeMessage::handle
			);
		}
	}
}
