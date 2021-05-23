package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaMessage {

	private int stamina = 0;
	private boolean exhausted = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeInt(this.stamina);
		packet.writeBoolean(this.exhausted);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncStaminaMessage decode(PacketBuffer packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
		message.exhausted = packet.readBoolean();
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
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;

			stamina.setStamina(this.stamina);
			stamina.setExhausted(this.exhausted);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerEntity player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;

		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = stamina.getStamina();
		message.exhausted = stamina.isExhausted();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
