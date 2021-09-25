package com.alrex.parcool.common.event;

import com.alrex.parcool.common.capability.IDodge;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.network.AvoidDamageMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerDamaged {
	@SubscribeEvent
	public static void onDamage(LivingAttackEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity)) return;
		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

		IDodge dodge = IDodge.get(player);
		if (dodge == null) return;
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		DamageSource damageSource = event.getSource();

		if (dodge.getDamageCoolTime() < 10) {
			event.setCanceled(true);
			return;
		}
		if (!stamina.isExhausted() && dodge.isDodging() && (dodge.getDirection() == IDodge.DodgeDirection.Front || dodge.getDirection() == IDodge.DodgeDirection.Back)) {
			if (!(damageSource.isFireDamage() || damageSource.isMagicDamage() || damageSource.isUnblockable()) && event.getAmount() < 15f) {
				AvoidDamageMessage.send(player, event.getAmount());
				dodge.resetDamageCoolTime();
				event.setCanceled(true);
			}
		}
	}
}
