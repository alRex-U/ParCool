package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class SyncStaminaMessage {

	private int stamina = 0;
	private boolean exhausted = false;
	private int clientDemandedMaxValue = 0;
	private UUID playerID = null;

	public void encode(FriendlyByteBuf packet) {
		packet.writeInt(this.stamina);
		packet.writeBoolean(this.exhausted);
		packet.writeInt(this.clientDemandedMaxValue);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncStaminaMessage decode(FriendlyByteBuf packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
		message.exhausted = packet.readBoolean();
		message.clientDemandedMaxValue = packet.readInt();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			Player player;
			player = context.getSender();
			ParCool.CHANNEL_INSTANCE.send(this, PacketDistributor.ALL.noArg());
			if (player == null) return;
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;
			stamina.set(this.stamina);
			stamina.setExhaustion(exhausted);
			stamina.setMaxStamina(clientDemandedMaxValue);
		});
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			Player player;
			if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				Level world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(playerID);
				if (player == null || player.isLocalPlayer()) return;
			} else {
				player = context.getSender();
				ParCool.CHANNEL_INSTANCE.send(this, PacketDistributor.ALL.noArg());
				if (player == null) return;
			}
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;
			stamina.set(this.stamina);
			stamina.setExhaustion(exhausted);
			stamina.setMaxStamina(clientDemandedMaxValue);
		});
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(Player player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null || !player.isLocalPlayer()) return;

		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = stamina.get();
		message.exhausted = stamina.isExhausted();
		message.playerID = player.getUUID();
		message.clientDemandedMaxValue = ParCoolConfig.Client.Integers.MaxStamina.get();

		ParCool.CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
	}
}
