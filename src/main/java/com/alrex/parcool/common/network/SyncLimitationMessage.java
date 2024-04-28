package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
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

	public void encode(FriendlyByteBuf packet) {
		packet.writeBytes(data);
		data.rewind();
	}

	public static SyncLimitationMessage decode(FriendlyByteBuf packet) {
		SyncLimitationMessage message = new SyncLimitationMessage();
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
			parkourability.getActionInfo().setServerLimitation(ServerLimitation.readFrom(data));
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void sync(ServerPlayer player) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		SyncLimitationMessage msg = new SyncLimitationMessage();
		parkourability.getActionInfo().getServerLimitation().writeTo(msg.data);
		msg.data.flip();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}
}
