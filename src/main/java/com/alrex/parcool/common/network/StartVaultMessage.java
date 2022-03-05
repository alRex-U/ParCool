package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartVaultMessage {
	UUID playerID = null;

	public UUID getPlayerID() {
		return playerID;
	}

	public void encode(PacketBuffer packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
	}

	public static StartVaultMessage decode(PacketBuffer packet) {
		StartVaultMessage message = new StartVaultMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				PlayerEntity player;
				if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
					World world = Minecraft.getInstance().level;
					if (world == null) return;
					player = world.getPlayerByUUID(playerID);
					if (player == null || player.isLocalPlayer()) return;
				} else {
					player = contextSupplier.get().getSender();
					ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
					if (player == null) return;
				}

				Parkourability parkourability = Parkourability.get(player);
				if (parkourability == null) return;
				parkourability.getVault().synchronize(this);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
		});
	}

	public static void send(PlayerEntity player) {
		StartVaultMessage message = new StartVaultMessage();
		message.playerID = player.getUUID();
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
