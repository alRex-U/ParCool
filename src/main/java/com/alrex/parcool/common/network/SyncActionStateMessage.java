package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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

	public void encode(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeByte(classNumber)
				.writeLong(senderUUID.getMostSignificantBits())
				.writeLong(senderUUID.getLeastSignificantBits())
				.writeInt(buffer.length)
				.writeBytes(buffer);
	}

	public static SyncActionStateMessage decode(FriendlyByteBuf packetBuffer) {
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
			Player player;

			player = contextSupplier.get().getSender();
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
			if (player == null) return;

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;

			Decoder decoder = new Decoder(this.buffer, parkourability);
			while (decoder.hasNext()) {
				Pair<Action, ByteBuffer> item = decoder.getItem();
				item.getFirst().restoreState(item.getSecond());
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				Level world = Minecraft.getInstance().level;
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

			Decoder decoder = new Decoder(this.buffer, parkourability);
			while (decoder.hasNext()) {
				Pair<Action, ByteBuffer> item = decoder.getItem();
				if (item.getFirst() != null) item.getFirst().restoreState(item.getSecond());
			}
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
		if (action instanceof Tap) return 10;
		if (action instanceof Flipping) return 11;
		if (action instanceof WallSlide) return 12;
		if (action instanceof HorizontalWallRun) return 13;

		return -1;
	}

	@Nullable
	private static Action getActionCorrespondingClassNumber(Parkourability parkourability, byte classNumber) {
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
			case 10:
				return parkourability.getTap();
			case 11:
				return parkourability.getFlipping();
			case 12:
				return parkourability.getWallSlide();
			case 13:
				return parkourability.getHorizontalWallRun();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(Player player, Builder builder) {
		ByteBuffer buffer1 = builder.build();
		if (buffer1.limit() == 0) return;
		SyncActionStateMessage message = new SyncActionStateMessage();
		message.senderUUID = player.getUUID();
		message.buffer = new byte[buffer1.limit()];
		buffer1.get(message.buffer);

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

	public static class Builder {
		private static final Builder instance = new Builder();
		private static final Builder sub = new Builder();

		private Builder() {
		}

		;
		private final ByteBuffer buffer = ByteBuffer.allocate(1024);

		public static Builder main() {
			instance.buffer.clear();
			return instance;
		}

		public static Builder sub() {
			sub.buffer.clear();
			return sub;
		}

		public Builder append(Action action, ByteBuffer actionBuffer) {
			buffer.put(getClassNumber(action))
					.putInt(actionBuffer.limit())
					.put(actionBuffer);
			return this;
		}

		public ByteBuffer build() {
			buffer.flip();
			return buffer;
		}
	}

	private static class Decoder {
		ByteBuffer buffer;
		Parkourability parkourability;

		Decoder(byte[] buf, Parkourability parkourability) {
			buffer = ByteBuffer.wrap(buf);
			this.parkourability = parkourability;
		}

		public boolean hasNext() {
			return buffer.position() < buffer.limit();
		}

		public Pair<Action, ByteBuffer> getItem() {
			Action action = getActionCorrespondingClassNumber(parkourability, buffer.get());
			byte[] array = new byte[buffer.getInt()];
			buffer.get(array);
			ByteBuffer buf = ByteBuffer.wrap(array);
			return new Pair<>(action, buf);
		}
	}
}
