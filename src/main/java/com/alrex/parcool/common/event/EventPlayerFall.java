package com.alrex.parcool.common.event;

import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.network.StartBreakfallMessage;
import com.alrex.parcool.config.ParCoolConfig;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerFall {
	@SubscribeEvent
	public static void onDamage(LivingFallEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		if (parkourability.get(BreakfallReady.class).isDoing()
				&& (parkourability.getClientInfo().getPossibilityOf(Tap.class)
				|| parkourability.getClientInfo().getPossibilityOf(Roll.class))
		) {
			boolean justTime = parkourability.get(BreakfallReady.class).getDoingTick() < 5;
			float distance = event.getDistance();
			var onlyJustTime = ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get();
			if (justTime || !onlyJustTime) {
				if (distance > 2) StartBreakfallMessage.send(player, justTime);
				if (distance < 6) {
					event.setCanceled(true);
				} else if (justTime) {
					event.setDamageMultiplier(event.getDamageMultiplier() * (justTime && !onlyJustTime ? 0.4f : 0.6f));
				}
			}
		}
	}
}
