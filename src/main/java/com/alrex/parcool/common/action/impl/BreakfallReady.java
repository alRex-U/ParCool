package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class BreakfallReady extends Action {
	@OnlyIn(Dist.CLIENT)
	public void startBreakfall(Player player, Parkourability parkourability, boolean justTimed) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
        boolean playSound = false;
		if (justTimed && ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get()) {
			if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
				player.playSound(SoundEvents.BREAKFALL_JUST_TIME.get(), 1, 1);
			if (ParCoolConfig.Client.Booleans.EnableActionParticles.get()
					&& ParCoolConfig.Client.Booleans.EnableActionParticlesOfJustTimeBreakfall.get()
			) {
				Vec3 pos = player.position();
				var rand = player.getRandom();
				for (int i = 0; i < 12; i++) {
					player.level().addParticle(ParticleTypes.END_ROD,
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

		var stamina = LocalStamina.get(localPlayer);
		if ((KeyBindings.isAnyMovingKeyDown()
				&& parkourability.getActionInfo().can(Roll.class))
				|| !parkourability.getActionInfo().can(Tap.class)
		) {
			stamina.consume(localPlayer, (int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Roll.class)));
            if (playSound) player.playSound(SoundEvents.ROLL.get(), 1, 1);
			parkourability.get(Roll.class).startRoll(player);
		} else {
			stamina.consume(localPlayer, (int) ((justTimed ? 0.25f : 1) * parkourability.getActionInfo().getStaminaConsumptionOf(Tap.class)));
            if (playSound) player.playSound(SoundEvents.SAFETY_TAP.get(), 1, 1);
			parkourability.get(Tap.class).startTap(player);
		}
	}

	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return canContinue(player, parkourability);
	}

	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return (KeyBindings.getKeyBreakfall().isDown()
                && !player.getData(Attachments.STAMINA).isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !player.isInWater()
				&& (!player.onGround() || parkourability.getAdditionalProperties().getLandingTick() < 3)
		);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.None;
	}
}
