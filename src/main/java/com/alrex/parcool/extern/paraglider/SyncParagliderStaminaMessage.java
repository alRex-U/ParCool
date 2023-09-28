package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.ParCool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import tictim.paraglider.api.stamina.Stamina;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncParagliderStaminaMessage {
	public enum Type {GIVE, TAKE, SET}

	private UUID playerID;
	private int value = 0;
	private Type commandType = Type.SET;

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeInt(this.value);
		packet.writeByte(this.commandType.ordinal());
	}

	public static SyncParagliderStaminaMessage decode(FriendlyByteBuf packet) {
		SyncParagliderStaminaMessage message = new SyncParagliderStaminaMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.value = packet.readInt();
		message.commandType = Type.values()[packet.readByte()];
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				player = Minecraft.getInstance().player;
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			player = player.getCommandSenderWorld().getPlayerByUUID(this.playerID);
			if (player == null) return;
			if (ParagliderManager.isParagliderInstalled()) {
				switch (commandType) {
					case SET: {
						Stamina.get(player).setStamina(value);
						break;
					}
					case GIVE: {
						Stamina.get(player).giveStamina(value, false);
						break;
					}
					case TAKE: {
						Stamina.get(player).takeStamina(value, false, false);
						break;
					}
				}
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player = contextSupplier.get().getSender();
			if (player == null) return;
			player = player.getCommandSenderWorld().getPlayerByUUID(this.playerID);
			if (player == null) return;
			if (ParagliderManager.isParagliderInstalled()) {
				switch (commandType) {
					case SET: {
						Stamina.get(player).setStamina(value);
						break;
					}
					case GIVE: {
						Stamina.get(player).giveStamina(value, false);
						break;
					}
					case TAKE: {
						Stamina.get(player).takeStamina(value, false, false);
						break;
					}
				}
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void send(AbstractClientPlayer player, int value, Type type) {
		SyncParagliderStaminaMessage message = new SyncParagliderStaminaMessage();
		message.playerID = player.getUUID();
		message.value = value;
		message.commandType = type;

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
	}
}
