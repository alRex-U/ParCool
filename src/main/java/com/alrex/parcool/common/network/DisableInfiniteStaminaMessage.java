package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.function.Supplier;

public class DisableInfiniteStaminaMessage {
	private boolean infiniteStamina;

	public void encode(FriendlyByteBuf packet) {
		packet.writeBoolean(infiniteStamina);
	}

	public static DisableInfiniteStaminaMessage decode(FriendlyByteBuf packet) {
		DisableInfiniteStaminaMessage message = new DisableInfiniteStaminaMessage();
		message.infiniteStamina = packet.readBoolean();
		return message;
	}

	@OnlyIn(Dist.CLIENT)
	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;

			Stamina stamina = Stamina.get(player);
			if (stamina == null) return;
			if (ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get()) {
				player.displayClientMessage(
						new TranslatableComponent(
								infiniteStamina ?
										TranslateKeys.MESSAGE_SERVER_ENABLE_INFINITE_STAMINA :
										TranslateKeys.MESSAGE_SERVER_DISABLE_INFINITE_STAMINA
						), false
				);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	public static void send(ServerPlayer player, boolean infiniteStamina) {
		DisableInfiniteStaminaMessage message = new DisableInfiniteStaminaMessage();
		message.infiniteStamina = infiniteStamina;
		ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
