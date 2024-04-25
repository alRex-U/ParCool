package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerCloneHandler {
	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event) {
		Player player = event.getPlayer();
		if (event.isWasDeath() && player instanceof ServerPlayerEntity) {
			Player from = event.getOriginal();
			Player to = player;
			Parkourability pFrom = Parkourability.get(from);
			Parkourability pTo = Parkourability.get(to);
			if (pFrom != null && pTo != null) {
				pTo.CopyFrom(pFrom);
			}
		}
	}
}
