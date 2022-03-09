package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class StartRollMessage {
	UUID playerID = null;

	public UUID getPlayerID() {
		return playerID;
	}

	public void encode(FriendlyByteBuf packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
	}

	public static StartRollMessage decode(FriendlyByteBuf packet) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketFlow.CLIENTBOUND) {
				Player player = Minecraft.getInstance().player;
				if (player == null) return;
				Player startPlayer = player.level.getPlayerByUUID(playerID);

				if (startPlayer == null) return;
				Parkourability parkourability = Parkourability.get(startPlayer);
				if (parkourability == null) return;

				parkourability.getRoll().synchronize(this);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
	}

	public static void send(ServerPlayer player) {
		StartRollMessage message = new StartRollMessage();
		message.playerID = player.getUUID();
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
