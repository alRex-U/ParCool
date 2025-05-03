package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.OtherStamina;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.NetworkContextWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaMessage {

	private int stamina = 0;
	private int max = 0;
	private boolean exhausted = false;
	private int consumeOnServer = 0;
	private int staminaType = -1;
	private UUID playerID = null;

	public void encode(PacketBuffer packet) {
		packet.writeInt(this.stamina);
		packet.writeInt(this.max);
		packet.writeBoolean(this.exhausted);
		packet.writeInt(this.staminaType);
		packet.writeInt(this.consumeOnServer);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncStaminaMessage decode(PacketBuffer packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
		message.max = packet.readInt();
		message.exhausted = packet.readBoolean();
		message.staminaType = packet.readInt();
		message.consumeOnServer = packet.readInt();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		Supplier<NetworkContextWrapper> supplier = NetworkContextWrapper.getSupplier(contextSupplier);
		supplier.get().enqueueWork(() -> {
			ServerPlayerWrapper player;
			player = ServerPlayerWrapper.get(supplier);
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
			if (player == null) return;
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;
			if (stamina instanceof OtherStamina) {
				((OtherStamina) stamina).setMax(this.max);
			}
			if (staminaType != -1 && consumeOnServer > 0) {
				IStamina.Type.values()[staminaType].handleConsumeOnServer(player, consumeOnServer);
			}
			stamina.set(this.stamina);
			stamina.setExhaustion(exhausted);
		});
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		Supplier<NetworkContextWrapper> supplier = NetworkContextWrapper.getSupplier(contextSupplier);
		supplier.get().enqueueWork(() -> {
			ServerPlayerWrapper serverPlayer = null;
			PlayerWrapper player;
			if (supplier.get().getReceptionSide() == LogicalSide.CLIENT) {
				LevelWrapper world = LevelWrapper.get();
				if (world == null) return;
				player = PlayerWrapper.get(world, playerID);
				if (player == null || player.isLocalPlayer()) return;
			} else {
				player = serverPlayer = ServerPlayerWrapper.get(supplier);
				ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
				if (player == null) return;
			}
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;
			if (stamina instanceof OtherStamina) {
				((OtherStamina) stamina).setMax(this.max);
			}
			if (serverPlayer != null && staminaType != -1 && consumeOnServer > 0) {
				IStamina.Type.values()[staminaType].handleConsumeOnServer(serverPlayer, consumeOnServer);
			}
			stamina.set(this.stamina);
			stamina.setExhaustion(exhausted);
		});
		supplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(PlayerWrapper player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null || !player.isLocalPlayer()) return;

		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = stamina.get();
		message.max = stamina.getActualMaxStamina();
		message.exhausted = stamina.isExhausted();
		message.consumeOnServer = stamina.getRequestedValueConsumedOnServer();
		IStamina.Type type = IStamina.Type.getFromInstance(stamina);
		message.staminaType = type != null ? type.ordinal() : -1;
		message.playerID = player.getUUID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
