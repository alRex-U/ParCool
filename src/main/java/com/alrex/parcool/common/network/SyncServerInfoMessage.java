package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class SyncServerInfoMessage {
	private final ByteBuffer limitationData = ByteBuffer.allocate(512);
	private int staminaValue;
	private boolean staminaExhausted;
	private boolean staminaNeedSync;


	public void encode(PacketBuffer packet) {
		packet.writeBoolean(staminaNeedSync);
		packet.writeBoolean(staminaExhausted);
		packet.writeInt(staminaValue);
		packet.writeBytes(limitationData);
		limitationData.rewind();
	}

	public static SyncServerInfoMessage decode(PacketBuffer packet) {
		SyncServerInfoMessage message = new SyncServerInfoMessage();
		message.staminaNeedSync = packet.readBoolean();
		message.staminaExhausted = packet.readBoolean();
		message.staminaValue = packet.readInt();
		while (packet.isReadable()) {
			message.limitationData.put(packet.readByte());
		}
		message.limitationData.flip();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			logReceived(player);
			parkourability.getActionInfo().setServerLimitation(ServerLimitation.readFrom(limitationData));
			if (staminaNeedSync) {
				IStamina stamina = IStamina.get(player);
				if (stamina == null) return;
				stamina.set(staminaValue);
				stamina.setExhaustion(staminaExhausted);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void logReceived(PlayerEntity player) {
		ParCool.LOGGER.log(Level.INFO, "Received Server Limitation of [" + player.getGameProfile().getName() + "]");
	}

	public static void logSent(PlayerEntity player) {
		ParCool.LOGGER.log(Level.INFO, "Sent Server Limitation of [" + player.getGameProfile().getName() + "]");
	}

    public static void sync(ServerPlayerEntity player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncServerInfoMessage msg = new SyncServerInfoMessage();
		parkourability.getActionInfo().getServerLimitation().writeTo(msg.limitationData);
		msg.limitationData.flip();
		msg.staminaNeedSync = false;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void syncWithStamina(ServerPlayerEntity player, IStamina stamina) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncServerInfoMessage msg = new SyncServerInfoMessage();
		parkourability.getActionInfo().getServerLimitation().writeTo(msg.limitationData);
		msg.limitationData.flip();
		msg.staminaNeedSync = true;
		{
			msg.staminaExhausted = stamina.isExhausted();
			msg.staminaValue = stamina.get();
		}
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
		logSent(player);
	}
}
