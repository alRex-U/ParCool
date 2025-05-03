package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.particles.ParticleTypes;

import java.nio.ByteBuffer;
import java.util.Random;

public class BreakfallReady extends Action {
	public void startBreakfall(PlayerWrapper player, Parkourability parkourability, IStamina stamina, boolean justTimed) {
		boolean playSound = false;
		if (justTimed && ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get()) {
			if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
                player.playSound(SoundEvents.BREAKFALL_JUST_TIME.get(), 1, 1);
			if (ParCoolConfig.Client.Booleans.EnableActionParticles.get()
					&& ParCoolConfig.Client.Booleans.EnableActionParticlesOfJustTimeBreakfall.get()
			) {
				Vec3Wrapper pos = player.position();
				Random rand = player.getRandom();
				for (int i = 0; i < 12; i++) {
					player.addParticle(ParticleTypes.END_ROD,
							pos.x(),
							pos.y() + player.getBbHeight() / 2,
							pos.z(),
							(rand.nextDouble() - 0.5) * 0.5,
							(rand.nextDouble() - 0.5) * 0.5,
							(rand.nextDouble() - 0.5) * 0.5
					);
				}
			}
		} else if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			playSound = true;

		if ((KeyBindings.isAnyMovingKeyDown()
				&& parkourability.getActionInfo().can(Roll.class))
				|| !parkourability.getActionInfo().can(Tap.class)
		) {
			stamina.consume((int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Roll.class)));
            if (playSound) player.playSound(SoundEvents.ROLL.get(), 1, 1);
			parkourability.get(Roll.class).startRoll(player);
		} else {
			stamina.consume((int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Tap.class)));
            if (playSound) player.playSound(SoundEvents.SAFETY_TAP.get(), 1, 1);
			parkourability.get(Tap.class).startTap(player);
		}
	}

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return (KeyBindings.getKeyBreakfall().isDown()
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isInWaterOrBubble()
				&& (!player.isOnGround() || parkourability.getAdditionalProperties().getLandingTick() < 3)
		);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
