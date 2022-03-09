package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncDodgeMessage {
	private UUID playerID = null;
	private boolean dodging = false;
	private boolean avoided = false;
	@Nullable
	private String dodgeDirection = null;

	public boolean isDodging() {
		return dodging;
	}

	public boolean isAvoided() {
		return avoided;
	}

	@Nullable
	public Dodge.DodgeDirections getDodgeDirection() {
		return dodgeDirection == null ? null : Dodge.DodgeDirections.valueOf(dodgeDirection);
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeBoolean(this.dodging);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
		packet.writeBoolean(this.avoided);
		packet.writeBoolean(dodgeDirection != null);
		if (dodgeDirection != null) {
			packet.writeInt(dodgeDirection.length());
			packet.writeCharSequence(dodgeDirection, StandardCharsets.UTF_8);
		}
	}

	public static SyncDodgeMessage decode(FriendlyByteBuf packet) {
		SyncDodgeMessage message = new SyncDodgeMessage();
		message.dodging = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.avoided = packet.readBoolean();
		if (packet.readBoolean()) {
			message.dodgeDirection = packet.readCharSequence(packet.readInt(), StandardCharsets.UTF_8).toString();
		}
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
			parkourability.getDodge().synchronize(this);
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
				player = world.getPlayerByUUID(playerID);
				if (player == null || player.isLocalPlayer()) return;
			} else {
				player = contextSupplier.get().getSender();
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (player == null) return;
			}

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			parkourability.getDodge().synchronize(this);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static SyncDodgeMessage getInstance(Player player, Dodge dodge) {
		SyncDodgeMessage message = new SyncDodgeMessage();
		message.dodging = dodge.isDodging();
		message.avoided = dodge.isAvoided();
		message.playerID = player.getUUID();
		Dodge.DodgeDirections direction = dodge.getDodgeDirection();
		if (direction != null) {
			message.dodgeDirection = direction.name();
		}
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(Player player, Dodge dodge) {
		SyncDodgeMessage message = getInstance(player, dodge);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void broadcast(Player player, Dodge dodge) {
		SyncDodgeMessage message = getInstance(player, dodge);
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
