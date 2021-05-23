package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICatLeap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncCatLeapMessage {
	private boolean isLeaping = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isLeaping);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncCatLeapMessage decode(PacketBuffer packet) {
		SyncCatLeapMessage message = new SyncCatLeapMessage();
		message.isLeaping = packet.readBoolean();
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
			ICatLeap catLeap = ICatLeap.get(player);
			if (catLeap == null) return;

			catLeap.setLeaping(this.isLeaping);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player) {
		ICatLeap catLeap = ICatLeap.get(player);
		if (catLeap == null) return;

		SyncCatLeapMessage message = new SyncCatLeapMessage();
		message.isLeaping = catLeap.isLeaping();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
