package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.client.particle.ParticleProvider;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.constants.TranslateKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class EventActivateParCool {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		if (KeyRecorder.keyActivateParCoolState.isPressed()) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null) return;
			Stamina stamina = Stamina.get(player);
			if (stamina == null) return;

			if (stamina.isExhausted()) {
				player.sendStatusMessage(new TranslationTextComponent(TranslateKeys.WARNING_ACTIVATION_EXHAUSTED), false);
				return;
			}
			boolean active = !ParCool.isActive();
			boolean can = active ? activate() : inactivate();
			if (!can) return;
			ParCool.setActivation(active);
			player.sendStatusMessage(new TranslationTextComponent(active ? TranslateKeys.MESSAGE_ACTIVATION_ACTIVE : TranslateKeys.MESSAGE_ACTIVATION_INACTIVE), false);
		}
	}

	public static boolean activate() {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return false;
		ParticleProvider.spawnEffectActivateParCool(player);
		return true;
	}

	public static boolean inactivate() {
		ClientPlayerEntity player = Minecraft.getInstance().player;

		return true;
	}
}
