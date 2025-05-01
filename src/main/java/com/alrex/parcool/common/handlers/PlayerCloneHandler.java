package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.ServerPlayerWrapper;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerCloneHandler {
	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event) {
		if (!event.isWasDeath() || ServerPlayerWrapper.getOrDefault(event) == null) return;
		PlayerWrapper from = PlayerWrapper.getOriginalPlayer(event);
		PlayerWrapper to = PlayerWrapper.get(event);
		Parkourability pFrom = Parkourability.get(from);
		Parkourability pTo = Parkourability.get(to);
		if (pFrom != null && pTo != null) {
			pTo.CopyFrom(pFrom);
		}
	}
}
