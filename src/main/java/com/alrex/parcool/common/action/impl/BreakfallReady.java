package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.nio.ByteBuffer;
import java.util.Random;

public class BreakfallReady extends Action {
	public void startBreakfall(Player player, Parkourability parkourability, IStamina stamina, boolean justTimed) {
		setDoing(false);
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get()) {
			player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1f, 0.7f);
		}
		
		var onlyJustTime = ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get();
				
		if (((KeyBindings.getKeyForward().isDown() 
				|| KeyBindings.getKeyBack().isDown() 
				|| KeyBindings.getKeyLeft().isDown() 
				|| KeyBindings.getKeyRight().isDown())
			&& parkourability.getActionInfo().can(Roll.class))
				|| !parkourability.getActionInfo().can(Tap.class)
		) {
			stamina.consume((int) ((justTimed && !onlyJustTime ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Roll.class)));
			if (justTimed || !onlyJustTime) {
				parkourability.get(Roll.class).startRoll(player);			
			}
		} else {
			stamina.consume((int) ((justTimed && !onlyJustTime ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Tap.class)));
			if (justTimed || !onlyJustTime) {
				parkourability.get(Tap.class).startTap(player);
			}
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
