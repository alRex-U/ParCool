package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerCloneHandler {
	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event) {
		PlayerEntity player = event.getPlayer();
		if (event.isWasDeath() && player instanceof ServerPlayerEntity) {
			PlayerEntity from = event.getOriginal();
			PlayerEntity to = event.getPlayer();
			Parkourability pFrom = Parkourability.get(from);
			Parkourability pTo = Parkourability.get(to);
			if (pFrom != null && pTo != null) {
				pTo.CopyFrom(pFrom);
			}
		}
	}
}
