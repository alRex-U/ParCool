package com.alrex.parcool.common.processor;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaLogic {
	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		PlayerEntity player = event.player;
		if (!ParCool.isActive()) return;
		IStamina stamina;
		{
			LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			if (!staminaOptional.isPresent()) return;
			stamina = staminaOptional.resolve().get();
		}
		if (stamina.isExhausted() && player.world.getRandom().nextInt(10) == 0 && player instanceof AbstractClientPlayerEntity) {
			ParticleProvider.spawnEffectSweat((AbstractClientPlayerEntity) player);
		}

		ClientPlayerEntity playerClient = Minecraft.getInstance().player;
		if (event.player != playerClient) return;

		if (playerClient.isCreative()) {
			stamina.setStamina(stamina.getMaxStamina());
			stamina.setExhausted(false);
			return;
		}
		if (stamina.getRecoveryCoolTime() <= 0) stamina.recover(stamina.getMaxStamina() / 100);
		stamina.updateRecoveryCoolTime();

		if (stamina.isExhausted()) {
			playerClient.setSprinting(false);
		}
	}
}
