package com.alrex.parcool.common.event;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.AvoidDamageMessage;
import com.alrex.parcool.common.network.StartRollMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerDamaged {
	@SubscribeEvent
	public static void onDamage(LivingDamageEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer)) return;
		ServerPlayer player = (ServerPlayer) event.getEntity();

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Stamina stamina = Stamina.get(player);
		if (stamina == null) return;
		DamageSource damageSource = event.getSource();

		if (
				damageSource.msgId.equals(DamageSource.FALL.msgId)
						&& parkourability.getRoll().isReady()
		) {
			StartRollMessage.send(player);
			float damage = event.getAmount();
			if (damage < 4) {
				event.setCanceled(true);
			} else {
				event.setAmount((damage - 4) / 2);
			}
			return;
		}
		Dodge dodge = parkourability.getDodge();
		if (dodge.isAvoided() && dodge.getDamageCoolTime() > 0) {
			event.setCanceled(true);
			return;
		}
		if (dodge.isDodging() &&
				(dodge.getDodgeDirection() == Dodge.DodgeDirections.Front ||
						dodge.getDodgeDirection() == Dodge.DodgeDirections.Back)
		) {
			if (!(damageSource.isFire() || damageSource.isMagic()) && event.getAmount() < 15f) {
				AvoidDamageMessage.send(player, event.getAmount());
				dodge.avoidDamage(player);
				event.setCanceled(true);
			}
		}
	}
}
