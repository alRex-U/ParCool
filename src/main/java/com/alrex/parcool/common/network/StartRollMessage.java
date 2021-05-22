package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.IRoll;
import com.alrex.parcool.common.processor.RollLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartRollMessage {
	private UUID playerID = null;

	private void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	private static StartRollMessage decode(PacketBuffer packet) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	private void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
				PlayerEntity startPlayer = clientPlayer.worldClient.getPlayerByUuid(playerID);

				IRoll roll;
				{
					LazyOptional<IRoll> rollOptional = startPlayer.getCapability(IRoll.RollProvider.ROLL_CAPABILITY);
					if (!rollOptional.isPresent()) return;
					roll = rollOptional.orElseThrow(NullPointerException::new);
				}
				if (!ParCoolConfig.CONFIG_CLIENT.canRoll.get() || !ParCoolConfig.CONFIG_CLIENT.ParCoolActivation.get())
					return;
				if (clientPlayer == startPlayer) {
					RollLogic.rollStart();
				} else {
					roll.setRollReady(false);
					roll.setRolling(true);
				}
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void send(ServerPlayerEntity player) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = player.getUniqueID();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}

	public static class MessageRegistry {
		private static final int ID = 2;

		@SubscribeEvent
		public static void register(FMLCommonSetupEvent event) {
			ParCool.CHANNEL_INSTANCE.registerMessage(
					ID,
					StartRollMessage.class,
					StartRollMessage::encode,
					StartRollMessage::decode,
					StartRollMessage::handle
			);
		}
	}
}
