package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;

public class StaminaControlMessage {
	private int value = 0;
	private boolean add = false;

	public void encode(FriendlyByteBuf packet) {
		packet.writeInt(this.value);
		packet.writeBoolean(this.add);
	}

	public static StaminaControlMessage decode(FriendlyByteBuf packet) {
		StaminaControlMessage message = new StaminaControlMessage();
		message.value = packet.readInt();
		message.add = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			Player player;

			if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				player = Minecraft.getInstance().player;
			} else {
				player = context.getSender();
			}
			if (player == null) return;
			IStamina stamina = IStamina.get(player);
			if (stamina == null) return;
			if (add) {
				stamina.recover(value);
			} else {
				stamina.set(value);
			}
		});
		context.setPacketHandled(true);
	}

	public static void sync(ServerPlayer player, int value, boolean add) {
		StaminaControlMessage message = new StaminaControlMessage();
		message.value = value;
		message.add = add;

		ParCool.CHANNEL_INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
	}
}
