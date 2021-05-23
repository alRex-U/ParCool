package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IFastRunning;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncFastRunningMessage {
	private boolean isFastRunning = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isFastRunning);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncFastRunningMessage decode(PacketBuffer packet) {
		SyncFastRunningMessage message = new SyncFastRunningMessage();
		message.isFastRunning = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				/*
				World world=Minecraft.getInstance().world;
				if (world==null)return;
				player=world.getPlayerByUuid(playerID);
				if (player==null||player.isUser())return;
				 */
				return;
			} else {
				player = contextSupplier.get().getSender();
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
			}
			IFastRunning fastRunning = IFastRunning.get(player);
			fastRunning.setFastRunning(this.isFastRunning);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player) {
		IFastRunning fastRunning = IFastRunning.get(player);
		SyncFastRunningMessage message = new SyncFastRunningMessage();
		message.isFastRunning = fastRunning.isFastRunning();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

}
