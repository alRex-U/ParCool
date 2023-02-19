package com.alrex.parcool.common.event;

import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.StartBreakfallMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerFall {
	@SubscribeEvent
	public static void onDamage(LivingFallEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		if (
				parkourability.get(BreakfallReady.class).isDoing()
		) {
			float distance = event.getDistance();
			if (distance > 2) StartBreakfallMessage.send(player);
			if (distance < 6) {
				event.setCanceled(true);
			} else {
				event.setDamageMultiplier(event.getDamageMultiplier() / 4);
			}
		}
	}
}
