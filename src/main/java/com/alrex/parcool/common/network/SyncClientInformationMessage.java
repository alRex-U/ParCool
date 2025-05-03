package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.compatibility.MinecraftServerWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncClientInformationMessage {
	private final ByteBuffer data = ByteBuffer.allocate(512);
	private UUID playerID = null;
	private boolean requestLimitations = false;

	public void encode(PacketBuffer packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeBoolean(requestLimitations);
		packet.writeBytes(data);
		data.rewind();
	}

	public static SyncClientInformationMessage decode(PacketBuffer packet) {
		SyncClientInformationMessage message = new SyncClientInformationMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.requestLimitations = packet.readBoolean();
		while (packet.isReadable()) {
			message.data.put(packet.readByte());
		}
		message.data.flip();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerWrapper player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				player = MinecraftServerWrapper.getPlayer(playerID);
				if (player == null) return;
			} else {
				ServerPlayerWrapper serverPlayer = ServerPlayerWrapper.get(contextSupplier);
				player = serverPlayer;
				if (player == null) return;
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (requestLimitations) {
					Limitations.update(serverPlayer);
				}
			}
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (!player.isLocalPlayer()) {
				logReceived(player);
                parkourability.getActionInfo().setClientSetting(ClientSetting.readFrom(data));
				data.rewind();
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayerWrapper player = ServerPlayerWrapper.get(contextSupplier);
			if (player == null) return;
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (requestLimitations) {
				Limitations.update(player);
			}
			logReceived(player);
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFrom(data));
			data.rewind();
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public void logReceived(PlayerWrapper player) {
		ParCool.LOGGER.log(Level.INFO, "Received Client Information of [" + player.getName() + "]");
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerWrapper player, boolean requestSendLimitation) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncClientInformationMessage message = new SyncClientInformationMessage();
		parkourability.getClientInfo().writeTo(message.data);
		message.data.flip();
		message.playerID = player.getUUID();
		message.requestLimitations = requestSendLimitation;

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
	}
}
