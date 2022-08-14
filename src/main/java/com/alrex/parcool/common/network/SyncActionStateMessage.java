package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.*;
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

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncActionStateMessage {
	private SyncActionStateMessage() {
	}

	;
	private byte classNumber = -1;
	private UUID senderUUID = null;
	private byte[] buffer = null;

	public void encode(PacketBuffer packetBuffer) {
		packetBuffer.writeByte(classNumber)
				.writeLong(senderUUID.getMostSignificantBits())
				.writeLong(senderUUID.getLeastSignificantBits())
				.writeInt(buffer.length)
				.writeBytes(buffer);
	}

	public static SyncActionStateMessage decode(PacketBuffer packetBuffer) {
		SyncActionStateMessage message = new SyncActionStateMessage();
		message.classNumber = packetBuffer.readByte();
		message.senderUUID = new UUID(packetBuffer.readLong(), packetBuffer.readLong());
		int size = packetBuffer.readInt();
		message.buffer = new byte[size];
		packetBuffer.readBytes(message.buffer);
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

			Action action = getActionCorrespondingClassNumber(parkourability);
			if (action != null) action.saveState(ByteBuffer.wrap(buffer));
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				World world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(senderUUID);
				if (player == null || player.isLocalPlayer()) return;
			} else {
				player = contextSupplier.get().getSender();
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (player == null) return;
			}

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;

			Action action = getActionCorrespondingClassNumber(parkourability);
			if (action != null) action.restoreState(ByteBuffer.wrap(buffer));
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static byte getClassNumber(Action action) {
		if (action instanceof AdditionalProperties) return 0;
		if (action instanceof CatLeap) return 1;
		if (action instanceof ClingToCliff) return 2;
		if (action instanceof Crawl) return 3;
		if (action instanceof Dodge) return 4;
		if (action instanceof FastRun) return 5;
		if (action instanceof Roll) return 6;
		if (action instanceof Vault) return 7;
		if (action instanceof WallJump) return 8;
		if (action instanceof Breakfall) return 9;

		return -1;
	}

	@Nullable
	private Action getActionCorrespondingClassNumber(Parkourability parkourability) {
		switch (classNumber) {
			case 0:
				return parkourability.getAdditionalProperties();
			case 1:
				return parkourability.getCatLeap();
			case 2:
				return parkourability.getClingToCliff();
			case 3:
				return parkourability.getCrawl();
			case 4:
				return parkourability.getDodge();
			case 5:
				return parkourability.getFastRun();
			case 6:
				return parkourability.getRoll();
			case 7:
				return parkourability.getVault();
			case 8:
				return parkourability.getWallJump();
			case 9:
				return parkourability.getBreakfall();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player, Action instance, ByteBuffer buffer) {
		SyncActionStateMessage message = new SyncActionStateMessage();
		message.senderUUID = player.getUUID();
		message.classNumber = getClassNumber(instance);
		message.buffer = new byte[buffer.limit()];
		buffer.get(message.buffer);

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
