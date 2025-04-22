package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LoginLogoutHandler {
	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		PlayerEntity player = event.getPlayer();
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (player instanceof ServerPlayerEntity) {
			Limitations.unload(player.getUUID());
		}
	}
	/*
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
	}
	*/
}
