package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IDodge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncDodgeMessage {
	private UUID playerID = null;
	private boolean isDodging = false;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isDodging);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncDodgeMessage decode(PacketBuffer packet) {
		SyncDodgeMessage message = new SyncDodgeMessage();
		message.isDodging = packet.readBoolean();
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
			if (player == null) return;
			IDodge dodge = IDodge.get(player);
			if (dodge == null) return;

			dodge.setDodging(this.isDodging);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player) {
		IDodge dodge = IDodge.get(player);
		if (dodge == null) return;

		SyncDodgeMessage message = new SyncDodgeMessage();
		message.isDodging = dodge.isDodging();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
