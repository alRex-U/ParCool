package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncRollMessage {
	private int readyTick = 0;
	private boolean rollReady = false;
	private boolean rolling = false;
	private UUID playerID = null;

	public boolean isRolling() {
		return rolling;
	}

	public boolean isRollReady() {
		return rollReady;
	}

	public int getReadyTick() {
		return readyTick;
	}

	public void encode(PacketBuffer packet) {
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
		packet.writeBoolean(rollReady);
		packet.writeBoolean(rolling);
		packet.writeInt(readyTick);
	}

	public static SyncRollMessage decode(PacketBuffer packet) {
		SyncRollMessage message = new SyncRollMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.rollReady = packet.readBoolean();
		message.rolling = packet.readBoolean();
		message.readyTick = packet.readInt();
		return message;
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			player = contextSupplier.get().getSender();
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);

			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getRoll().synchronize(this);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().world;
				if (world == null) return;
				player = world.getPlayerByUuid(playerID);
				if (player == null || player.isUser()) return;
			} else {
				player = contextSupplier.get().getSender();
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (player == null) return;
			}

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getRoll().synchronize(this);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player, Roll roll) {
		SyncRollMessage message = new SyncRollMessage();
		message.rollReady = roll.isReady();
		message.readyTick = roll.getReadyTick();
		message.rolling = roll.isRolling();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
