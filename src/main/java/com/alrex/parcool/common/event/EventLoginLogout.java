package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.network.PlayerLoginEventMessage;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventLoginLogout {
	@SubscribeEvent
	public static void LoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			Limitations.load(player.getUUID());
			parkourability.getActionInfo().setServerLimitation(ServerLimitation.get(serverPlayer));
			PlayerLoginEventMessage.send(serverPlayer);
		}
	}

	@SubscribeEvent
	public static void LogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		PlayerEntity player = event.getPlayer();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (player instanceof ServerPlayerEntity) {
			Limitations.unload(player.getUUID());
		}
	}
}
