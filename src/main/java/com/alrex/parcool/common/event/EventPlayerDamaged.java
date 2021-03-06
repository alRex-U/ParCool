package com.alrex.parcool.common.event;

import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.AvoidDamageMessage;
import com.alrex.parcool.common.network.StartRollMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerDamaged {
	@SubscribeEvent
	public static void onDamage(LivingDamageEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Stamina stamina = Stamina.get(player);
		if (stamina == null) return;
		DamageSource damageSource = event.getSource();

		if (
				damageSource.getDamageType().equals(DamageSource.FALL.getDamageType())
						&& parkourability.getRoll().isReady()
		) {
			StartRollMessage.send(player);
			float damage = event.getAmount();
			if (damage < 2) {
				event.setCanceled(true);
			} else {
				event.setAmount((damage - 2) / 2);
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
			if (!(damageSource.isFireDamage() || damageSource.isMagicDamage() || damageSource.isUnblockable()) && event.getAmount() < 15f) {
				AvoidDamageMessage.send(player, event.getAmount());
				dodge.avoidDamage(player);
				event.setCanceled(true);
			}
		}
	}
}
