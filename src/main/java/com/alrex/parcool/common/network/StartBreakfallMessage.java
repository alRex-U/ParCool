package com.alrex.parcool.common.network;

import com.alrex.parcool.api.compatibility.ChannelInstanceWrapper;
import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StartBreakfallMessage {
	UUID playerID = null;
	boolean justTimed = false;

	public UUID getPlayerID() {
		return playerID;
	}

	public void encode(PacketBuffer packet) {
		packet.writeLong(playerID.getMostSignificantBits());
		packet.writeLong(playerID.getLeastSignificantBits());
		packet.writeBoolean(justTimed);
	}

	public static StartBreakfallMessage decode(PacketBuffer packet) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		message.justTimed = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			if (contextSupplier.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
				PlayerWrapper player = ClientPlayerWrapper.get();
				if (player == null) return;
				if (!playerID.equals(player.getUUID())) return;

				Parkourability parkourability = Parkourability.get(player);
				if (parkourability == null) return;
				IStamina stamina = IStamina.get(player);
				if (stamina == null) return;

				parkourability.get(BreakfallReady.class).startBreakfall(player, parkourability, stamina, justTimed);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
	}

	public static void send(ServerPlayerWrapper player, boolean justTimed) {
		StartBreakfallMessage message = new StartBreakfallMessage();
		message.playerID = player.getUUID();
		message.justTimed = justTimed;
		ChannelInstanceWrapper.send(player, message);
	}
}
