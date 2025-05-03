package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.compatibility.NetworkContextWrapper;
import com.alrex.parcool.compatibility.PlayerPacketDistributor;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
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
		Supplier<NetworkContextWrapper> supplier = NetworkContextWrapper.getSupplier(contextSupplier);
		supplier.get().enqueueWork(() -> {
			ClientPlayerWrapper player = ClientPlayerWrapper.get();
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
		supplier.get().setPacketHandled(true);
	}

	public void logReceived(PlayerWrapper player) {
		ParCool.LOGGER.log(Level.INFO, "Received Server Limitation of [" + player.getName() + "]");
	}

    public static void sync(ServerPlayerWrapper player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncServerInfoMessage msg = new SyncServerInfoMessage();
		parkourability.getActionInfo().getServerLimitation().writeTo(msg.limitationData);
		msg.limitationData.flip();
		msg.staminaNeedSync = false;
		ParCool.CHANNEL_INSTANCE.send(PlayerPacketDistributor.with(player), msg);
	}

	public static void syncWithStamina(ServerPlayerWrapper player, IStamina stamina) {
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
		ParCool.CHANNEL_INSTANCE.send(PlayerPacketDistributor.with(player), msg);
	}
}
