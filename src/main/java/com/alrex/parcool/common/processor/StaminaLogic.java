package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StaminaLogic {
	static byte count = 0;

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		PlayerEntity player = event.player;
		if (!ParCool.isActive()) return;
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		if (stamina.isExhausted() && player.world.getRandom().nextInt(10) == 0 && player instanceof AbstractClientPlayerEntity) {
			ParticleProvider.spawnEffectSweat((AbstractClientPlayerEntity) player);
		}

		if (!player.isUser()) return;
		count++;
		if (count >= 20) {
			count = 0;

		}

		if (player.isCreative() || (stamina.isAllowedInfiniteStamina() && ParCoolConfig.CONFIG_CLIENT.infiniteStamina.get())) {
			stamina.setStamina(stamina.getMaxStamina());
			stamina.setExhausted(false);
			return;
		}
		if (stamina.getRecoveryCoolTime() <= 0) stamina.recover(stamina.getMaxStamina() / 100);
		stamina.updateRecoveryCoolTime();

		if (stamina.isExhausted()) {
			player.setSprinting(false);
		}
	}
}
