package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IRoll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncRollReadyMessage {
	private boolean rollReady = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
		packet.writeBoolean(rollReady);
	}

	public static SyncRollReadyMessage decode(PacketBuffer packet) {
		SyncRollReadyMessage message = new SyncRollReadyMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.rollReady = packet.readBoolean();
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				player = Minecraft.getInstance().world.getPlayerByUuid(playerID);
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			IRoll roll = IRoll.get(player);
			if (roll == null) return;

			roll.setRollReady(rollReady);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerEntity player) {
		IRoll roll = IRoll.get(player);
		if (roll == null) return;

		SyncRollReadyMessage message = new SyncRollReadyMessage();
		message.rollReady = roll.isRollReady();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
