package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class StaminaControlMessage {
	private int value = 0;
	private boolean add = false;

	public void encode(PacketBuffer packet) {
		packet.writeInt(this.value);
		packet.writeBoolean(this.add);
	}

	public static StaminaControlMessage decode(PacketBuffer packet) {
		StaminaControlMessage message = new StaminaControlMessage();
		message.value = packet.readInt();
		message.add = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			PlayerEntity player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				player = Minecraft.getInstance().player;
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			Stamina stamina = Stamina.get(player);
			if (stamina == null) return;
			if (add) {
				stamina.recover(value);
			} else {
				stamina.setStamina(value);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void sync(ServerPlayerEntity player, int value, boolean add) {
		StaminaControlMessage message = new StaminaControlMessage();
		message.value = value;
		message.add = add;

		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
