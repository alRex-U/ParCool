package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class StartBreakfallMessage {
	UUID playerID = null;
	boolean justTimed = false;

	public UUID getPlayerID() {
		return playerID;
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeBoolean(justTimed);
	}

	public static StartBreakfallMessage decode(FriendlyByteBuf packet) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.justTimed = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			if (context.isClientSide()) {
				Player player = Minecraft.getInstance().player;
				if (player == null) return;
				if (!playerID.equals(player.getUUID())) return;

				Parkourability parkourability = Parkourability.get(player);
				if (parkourability == null) return;
				IStamina stamina = IStamina.get(player);
				if (stamina == null) return;

				parkourability.get(BreakfallReady.class).startBreakfall(player, parkourability, stamina, justTimed);
			}
		});
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(CustomPayloadEvent.Context context) {
	}

	public static void send(ServerPlayer player, boolean justTimed) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = player.getUUID();
		message.justTimed = justTimed;
		ParCool.CHANNEL_INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
	}
}
