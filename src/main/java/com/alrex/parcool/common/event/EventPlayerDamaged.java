package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.IDodge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerDamaged {
	@SubscribeEvent
	public static void onDamage(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity) event.getEntity();

		IDodge dodge = IDodge.get(player);
		if (dodge == null) return;

		if (dodge.isDodging() && dodge.getDirection() == IDodge.DodgeDirection.Front) {
			if (event.getSource().isProjectile() && event.getAmount() < 5.0f) {
				event.setCanceled(true);
			}
		}
	}
}
