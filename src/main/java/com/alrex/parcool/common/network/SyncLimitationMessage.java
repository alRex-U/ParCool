package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.Limitations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class SyncLimitationMessage {
	private final ByteBuffer data = ByteBuffer.allocate(512);
	private boolean forIndividuals = false;

	public void encode(FriendlyByteBuf packet) {
		packet.writeBoolean(forIndividuals);
		packet.writeBytes(data);
		data.rewind();
	}

	public static SyncLimitationMessage decode(FriendlyByteBuf packet) {
		SyncLimitationMessage message = new SyncLimitationMessage();
		message.forIndividuals = packet.readBoolean();
		while (packet.isReadable()) {
			message.data.put(packet.readByte());
		}
		message.data.flip();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			if (forIndividuals) {
				parkourability.getActionInfo().getIndividualLimitation().readFrom(data);
				data.rewind();
			} else {
				parkourability.getActionInfo().getServerLimitation().readFrom(data);
				data.rewind();
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	private static SyncLimitationMessage newInstance(Limitations limitation) {
		SyncLimitationMessage message = new SyncLimitationMessage();
		limitation.writeTo(message.data);
		message.data.flip();
		return message;
	}

	public static void sendServerLimitation(ServerPlayer player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getActionInfo().getServerLimitation().readFromServerConfig();
		parkourability.getActionInfo().getServerLimitation().setReceived();
		SyncLimitationMessage msg = newInstance(parkourability.getActionInfo().getServerLimitation());
		msg.forIndividuals = false;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendIndividualLimitation(ServerPlayer player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getActionInfo().getIndividualLimitation().setReceived();
		SyncLimitationMessage msg = newInstance(parkourability.getActionInfo().getIndividualLimitation());
		msg.forIndividuals = true;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}
}
