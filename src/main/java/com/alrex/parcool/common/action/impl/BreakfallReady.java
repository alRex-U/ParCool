package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;

import java.nio.ByteBuffer;
import java.util.Random;

public class BreakfallReady extends Action {
	public void startBreakfall(PlayerEntity player, Parkourability parkourability, IStamina stamina, boolean justTimed) {
		setDoing(false);
		if (justTimed && ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get()) {
			player.playSound(SoundEvents.ANVIL_PLACE, 0.75f, 2f);
			Vector3d pos = player.position();
			Random rand = player.getRandom();
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
		}
		if (((KeyBindings.getKeyForward().isDown() || KeyBindings.getKeyBack().isDown())
				&& parkourability.getActionInfo().can(Roll.class))
				|| !parkourability.getActionInfo().can(Tap.class)
		) {
			stamina.consume((int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Roll.class)));
			parkourability.get(Roll.class).startRoll(player);
		} else {
			stamina.consume((int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Tap.class)));
			parkourability.get(Tap.class).startTap(player);
		}
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
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
