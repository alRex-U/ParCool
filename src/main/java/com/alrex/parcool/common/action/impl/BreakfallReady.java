package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.nio.ByteBuffer;

public class BreakfallReady extends Action {
	public void startBreakfall(Player player, Parkourability parkourability, IStamina stamina, boolean justTimed) {
		setDoing(false);
        boolean playSound = false;
		if (justTimed && ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get()) {
			if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
                player.playSound(SoundEvents.BREAKFALL_JUST_TIME.get(), 1, 1);
			Vec3 pos = player.position();
			var rand = player.getRandom();
			for (int i = 0; i < 12; i++) {
				player.level.addParticle(ParticleTypes.END_ROD,
						pos.x(),
						pos.y() + player.getBbHeight() / 2,
						pos.z(),
						(rand.nextDouble() - 0.5) * 0.5,
						(rand.nextDouble() - 0.5) * 0.5,
						(rand.nextDouble() - 0.5) * 0.5
				);
			}
		} else if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            playSound = true;

		if (((KeyBindings.getKeyForward().isDown() || KeyBindings.getKeyBack().isDown() || KeyBindings.getKeyLeft().isDown() || KeyBindings.getKeyRight().isDown())
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
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
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
