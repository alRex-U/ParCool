package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LoginLogoutHandler {
	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		PlayerWrapper player = PlayerWrapper.get(event);
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (ServerPlayerWrapper.is(player)) {
			Limitations.unload(player.getUUID());
		}
	}
	/*
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
	}
	*/
}
