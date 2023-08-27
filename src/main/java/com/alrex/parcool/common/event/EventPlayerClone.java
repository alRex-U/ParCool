package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

;

public class EventPlayerClone {
	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			Player from = event.getOriginal();
			Player to = event.getPlayer();
			Parkourability pFrom = Parkourability.get(from);
			Parkourability pTo = Parkourability.get(to);
			if (pFrom != null && pTo != null) {
				pTo.CopyFrom(pFrom);
			}
		}
	}
}
