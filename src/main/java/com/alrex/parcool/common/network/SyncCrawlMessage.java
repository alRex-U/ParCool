package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.ICrawl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncCrawlMessage {

	private boolean isCrawling = false;
	private boolean isSliding = false;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeBoolean(this.isCrawling);
		packet.writeBoolean(this.isSliding);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncCrawlMessage decode(PacketBuffer packet) {
		SyncCrawlMessage message = new SyncCrawlMessage();
		message.isCrawling = packet.readBoolean();
		message.isSliding = packet.readBoolean();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				player = Minecraft.getInstance().world.getPlayerByUuid(playerID);

			} else {
				player = contextSupplier.get().getSender();
			}
			ICrawl crawl = ICrawl.get(player);
			if (crawl == null) return;

			crawl.setCrawling(this.isCrawling);
			crawl.setSliding(this.isSliding);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(ClientPlayerEntity player) {
		ICrawl crawl = ICrawl.get(player);

		SyncCrawlMessage message = new SyncCrawlMessage();
		message.isCrawling = crawl.isCrawling();
		message.isSliding = crawl.isSliding();
		message.playerID = player.getUniqueID();

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
