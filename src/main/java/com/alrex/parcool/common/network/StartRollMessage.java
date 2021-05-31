package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.IRoll;
import com.alrex.parcool.common.processor.RollLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartRollMessage {
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static StartRollMessage decode(PacketBuffer packet) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				World world = Minecraft.getInstance().world;
				if (world == null) return;
				PlayerEntity startPlayer = world.getPlayerByUuid(playerID);
				if (startPlayer == null) return;

				IRoll roll = IRoll.get(startPlayer);
				if (roll == null) return;

				if (!ParCoolConfig.CONFIG_CLIENT.canRoll.get() || !ParCoolConfig.CONFIG_CLIENT.ParCoolActivation.get())
					return;
				if (startPlayer.isUser()) {
					RollLogic.rollStart();
				} else {
					roll.setRollReady(false);
					roll.setRolling(true);
				}
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
	}

	public static void send(ServerPlayerEntity player) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = player.getUniqueID();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
