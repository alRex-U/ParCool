package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SyncClientInformationMessage {
	private final ByteBuffer data = ByteBuffer.allocate(512);
	private UUID playerID = null;
	private boolean requestLimitations = false;

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeBoolean(requestLimitations);
		packet.writeBytes(data);
		data.rewind();
	}

	public static SyncClientInformationMessage decode(FriendlyByteBuf packet) {
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
	public void handleClient(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			Player player;
			if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				Level world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(playerID);
				if (player == null) return;
			} else {
				ServerPlayer serverPlayer = context.getSender();
				player = serverPlayer;
				if (player == null) return;
				ParCool.CHANNEL_INSTANCE.send(this, PacketDistributor.ALL.noArg());
				if (requestLimitations) {
                    Limitations.update(serverPlayer);
				}
			}
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (!player.isLocalPlayer()) {
                parkourability.getActionInfo().setClientSetting(ClientSetting.readFrom(data));
				data.rewind();
			}
		});
		context.setPacketHandled(true);
	}

	public void handleServer(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) return;
			ParCool.CHANNEL_INSTANCE.send(this, PacketDistributor.ALL.noArg());

			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (requestLimitations) {
                Limitations.update(player);
			}
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFrom(data));
			data.rewind();
		});
		context.setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(LocalPlayer player, boolean requestSendLimitation) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncClientInformationMessage message = new SyncClientInformationMessage();
		parkourability.getClientInfo().writeTo(message.data);
		message.data.flip();
		message.playerID = player.getUUID();
		message.requestLimitations = requestSendLimitation;

		ParCool.CHANNEL_INSTANCE.send(message, PacketDistributor.SERVER.noArg());
	}
}
