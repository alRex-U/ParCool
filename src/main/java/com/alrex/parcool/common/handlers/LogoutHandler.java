package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LogoutHandler {
	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (player instanceof ServerPlayer) {
			Limitations.unload(player.getUUID());
		}
	}
}
