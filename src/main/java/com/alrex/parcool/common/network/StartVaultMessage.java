package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartVaultMessage {
	UUID playerID;
	Vault.Type type;

	StartVaultMessage(UUID id, Vault.Type type) {
		this.playerID = id;
		this.type = type;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public Vault.Type getType() {
		return type;
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeInt(type.getCode());
	}

	public static StartVaultMessage decode(FriendlyByteBuf packet) {
		StartVaultMessage message = new StartVaultMessage(
				new UUID(packet.readLong(), packet.readLong()),
				Vault.Type.get(packet.readInt())
		);
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketFlow.CLIENTBOUND) {
				Player player;
				if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
					Level world = Minecraft.getInstance().level;
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

	public static void send(Player player, Vault.Type type) {
		StartVaultMessage message = new StartVaultMessage(player.getUUID(), type);
		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
